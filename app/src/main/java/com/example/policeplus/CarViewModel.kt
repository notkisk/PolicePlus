package com.example.policeplus

import RetrofitInstance.api
import UserPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.policeplus.models.CarEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.example.policeplus.models.Car

import org.apache.commons.text.similarity.LevenshteinDistance
import org.apache.commons.text.similarity.JaroWinklerSimilarity
import kotlin.math.max

@HiltViewModel
class CarViewModel @Inject constructor(
    application: Application,
    private val repository: CarRepository,
    private val carManager: CarManager
) : AndroidViewModel(application) {

    private val userPreferences = UserPreferences(application)

    private val _car = MutableLiveData<Car?>(null)
    val car: LiveData<Car?> = _car

    private val _carHistory = MutableStateFlow<List<Car>>(emptyList())
    val carHistory: StateFlow<List<Car>> = _carHistory

    val latestScans: StateFlow<List<Car>> = _carHistory
        .map { it.take(2) }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isTicketSubmissionLoading = MutableStateFlow<Boolean>(false)
    val isTicketSubmissionLoading: StateFlow<Boolean> = _isTicketSubmissionLoading

    private val _error = MutableLiveData("")
    val error: LiveData<String> = _error

    private var userEmail: String = ""
    private var carObserver: LiveData<List<CarEntity>>? = null

    private val _allCars = MutableLiveData<List<CarEntity>>(emptyList())
    val allCars: LiveData<List<CarEntity>> = _allCars

    private val _showDeleteConfirmationDialog = MutableStateFlow(false)
    val showDeleteConfirmationDialog: StateFlow<Boolean> = _showDeleteConfirmationDialog

    init {
        loadUserAndHistory()
        // Listen for login events
        viewModelScope.launch {
            carManager.loginEvent.collect {
                loadUserAndHistory()
            }
        }
    }

    fun loadUserAndHistory() {
        viewModelScope.launch {
            val user = userPreferences.getUser()
            user?.let { it ->
                userEmail = it.email
                // Remove previous observer if it exists
                carObserver?.let { observer ->
                    observer.removeObserver { }
                }
                // Create new observer for current user
                carObserver = if (it.userType == "police") {
                    // Police can see all cars
                    repository.getCarsByUser(userEmail)
                } else {
                    // Normal users only see their own cars
                   // if (user.userType == "normal")user.licenseNumber?.let { it1 -> fetchCar(it1) }
                    repository.getCarsByUser(it.email)
                }
                carObserver?.observeForever { cars ->
                    val uniqueCars = cars?.distinctBy { it.licenseNumber } ?: emptyList()
                    _allCars.postValue(uniqueCars)
                    _carHistory.value = uniqueCars.map { it.toCar() }
                }
            } ?: run {
                // Clear UI state when no user is logged in
                _allCars.postValue(emptyList())
                _carHistory.value = emptyList()
                carObserver?.let { observer ->
                    observer.removeObserver { }
                }
                carObserver = null
            }
        }
    }

    fun clearUserData() {
        viewModelScope.launch {
            _allCars.postValue(emptyList())
            _carHistory.value = emptyList()
            carObserver?.let { observer ->
                observer.removeObserver { }
            }
            carObserver = null
        }
    }

    private suspend fun insert(car: CarEntity) {
        repository.insertCar(car)
    }

    fun fetchCar(licensePlate: String) {
        viewModelScope.launch {
            _isLoading.postValue(true)
            _error.postValue("")
            try {
                Log.d("CarFetch", "Fetching car with license: $licensePlate")
                val response = api.getCarByPlate(licensePlate)
                Log.d("CarFetch", "Response code: ${response.code()}")
                Log.d("CarFetch", "Response message: ${response.message()}")
                Log.d("CarFetch", "Raw response: ${response.raw()}")
                
                if (response.isSuccessful) {
                    Log.d("CarFetch", "API call successful")
                    response.body()?.let { carData ->
                        Log.d("CarFetch", "Car data received: $carData")
                        val user = userPreferences.getUser()
                        Log.d("CarFetch", "User type: ${user?.userType}, User name: ${user?.name}")
                        if (user?.userType == "police") {
                            Log.d("CarFetch", "Processing as police user")
                            _car.postValue(carData)
                            insert(carData.toEntity(userEmail))
                        } else {
                            Log.d("CarFetch", "Processing as normal user")
                            Log.d("CarFetch", "Car owner: ${carData.owner}")
                            Log.d("CarFetch", "User name: ${user?.name}")
                            
                            if (/*areNamesSimilar(carData.owner, user?.name ?: "")*/ true) {
                                Log.d("CarFetch", "Names are similar, proceeding with car addition")
                                _car.postValue(carData)
                                val existingCar = repository.getCarByLicense(licensePlate, user?.email ?: "")
                                if (existingCar.value == null) {
                                    Log.d("CarFetch", "Car doesn't exist yet, adding new car")
                                    if (user != null) {
                                        insert(carData.toEntity(user.email))
                                        Log.d("CarFetch", "Car inserted successfully")
                                        loadUserAndHistory() // Force refresh after insertion
                                    }
                                } else {
                                    Log.d("CarFetch", "Car already exists")
                                    _error.postValue("You already have this car in your list")
                                }
                            } else {
                                Log.d("CarFetch", "Names are not similar, access denied")
                                _error.postValue("You do not have permission to view this car")
                                loadUserAndHistory()
                            }
                        }
                    } ?: run {
                        Log.d("CarFetch", "Response body is null")
                        _error.postValue("No car data received")
                    }
                } else {
                    Log.d("CarFetch", "API call failed: ${response.code()} - ${response.message()}")
                    Log.d("CarFetch", "Error body: ${response.errorBody()?.string()}")
                    _error.postValue("Failed to fetch car: ${response.message()}")
                    loadUserAndHistory()
                }
            } catch (e: Exception) {
                Log.d("CarFetch", "Exception occurred: ${e.message}")
                Log.d("CarFetch", "Stack trace: ${e.stackTraceToString()}")
                _error.postValue("Error fetching car data: ${e.message}")
                loadUserAndHistory()
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun submitTicket(ticket: Ticket){
        viewModelScope.launch {
            _isTicketSubmissionLoading.value = true
            try {
                val response =   api.submitTicket(ticket = ticket)
                if(response.isSuccessful){
                    _isTicketSubmissionLoading.value = false

                    response.body()?.let {
                        Log.d("Ticket",it.message)
                    }

                }
            }catch (e:Exception){

            }

        }
    }


    fun reportStolenCar(stolenReport: StolenReport){
        viewModelScope.launch {
            _isTicketSubmissionLoading.value = true
            try {
                val response =   api.reportStolenCar( stolenReport,stolenReport.license_plate,stolenReport.stolen_status)
                if(response.isSuccessful){
                    _isTicketSubmissionLoading.value = false

                    response.body()?.let {
                        Log.d("Ticket",it.message)
                    }

                }
            }catch (e:Exception){

            }

        }
    }


    fun deleteACar(car: Car) {
        viewModelScope.launch {
            // Get current user's email
            val user = userPreferences.getUser()
            user?.let { currentUser ->
                repository.deleteCar(car.id.toString(), currentUser.email)
                // Reload user's cars after deletion
                loadUserAndHistory()
            }
        }
    }

    fun setShowDeleteConfirmationDialog(show: Boolean) {
        viewModelScope.launch {
            _showDeleteConfirmationDialog.emit(show)
        }
    }

    fun getCarsByUser(email: String): LiveData<List<CarEntity>> {
        return repository.getCarsByUser(email)
    }
}

fun Car.toEntity(userEmail: String): CarEntity {
    return CarEntity(
        licenseNumber = this.licenseNumber,
        owner = this.owner,
        insuranceStart = this.insuranceStart,
        insuranceEnd = this.insuranceEnd,
        inspectionStart = this.inspectionStart,
        inspectionEnd = this.inspectionEnd,
        taxPaid = this.taxPaid,
        stolenCar = this.stolenCar,
        makeAndModel = this.makeAndModel,
        color = this.color,
        driverLicense = this.driverLicense,
        address = this.address,
        scanDate = System.currentTimeMillis(),
        userEmail = userEmail,
    )
}

fun CarEntity.toCar(): Car {
    return Car(
        id = this.id,
        licenseNumber = this.licenseNumber,
        owner = this.owner,
        insuranceStart = this.insuranceStart,
        insuranceEnd = this.insuranceEnd,
        inspectionStart = this.inspectionStart,
        inspectionEnd = this.inspectionEnd,
        taxPaid = this.taxPaid,
        stolenCar = this.stolenCar,
        makeAndModel = this.makeAndModel,
        color = this.color,
        driverLicense = this.driverLicense,
        address = this.address,
        scanDate = this.scanDate
    )
}
fun areNamesSimilar(name1: String, name2: String, levenshteinThreshold: Int = 50, jaroThreshold: Double = 0.50): Boolean {
    if (name1.isBlank() || name2.isBlank()) return false

    val levenshteinDistance = LevenshteinDistance().apply(name1, name2)
    val maxLength = max(name1.length, name2.length)
    val levenshteinSimilarity = ((maxLength - levenshteinDistance) / maxLength.toDouble()) * 100

    val jaroSimilarity = JaroWinklerSimilarity().apply(name1, name2)

    return levenshteinSimilarity >= levenshteinThreshold || jaroSimilarity >= jaroThreshold
}