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
    
    private val _tickets = MutableStateFlow<List<com.example.policeplus.models.Ticket>>(emptyList())
    val tickets: StateFlow<List<com.example.policeplus.models.Ticket>> = _tickets
    
    private val _carWithTickets = MutableStateFlow<Car?>(null)
    val carWithTickets: StateFlow<Car?> = _carWithTickets

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
            try {
                val user = userPreferences.getUser()
                if (user != null) {
                    userEmail = user.email
                    
                    // Remove previous observer if it exists
                    carObserver?.let { observer ->
                        observer.removeObserver {}
                    }
                    
                    // Set up the observer for car updates
                    carObserver = repository.getCarsByUser(user.email)
                    
                    carObserver?.observeForever { cars ->
                        viewModelScope.launch {
                            try {
                                val uniqueCars = cars.distinctBy { it.licenseNumber }
                                _allCars.postValue(uniqueCars)
                                
                                if (uniqueCars.isNotEmpty()) {
                                    // For vehicle owners, we'll take the first car (they should only have one)
                                    val carEntity = uniqueCars.first()
                                    val tickets = repository.getTicketsForCarSync(carEntity.id)
                                    val carWithTickets = carEntity.toCar().copy(tickets = tickets)
                                    _car.postValue(carWithTickets)
                                    
                                    // Also update the car history with the latest data
                                    _carHistory.value = uniqueCars.map { it.toCar() }
                                    
                                    // Update current car if it exists in the new data
                                    _car.value?.let { currentCar ->
                                        uniqueCars.find { it.id == currentCar.id }?.let { updatedCar ->
                                            _car.value = updatedCar.toCar().copy(tickets = tickets)
                                        }
                                    }
                                } else {
                                    _car.postValue(null)
                                    _carHistory.value = emptyList()
                                }
                            } catch (e: Exception) {
                                Log.e("CarViewModel", "Error processing car data", e)
                                _error.postValue("Error processing car data: ${e.message}")
                            }
                        }
                    }
                } else {
                    // Clear UI state when no user is logged in
                    _allCars.postValue(emptyList())
                    _carHistory.value = emptyList()
                    carObserver?.let { observer ->
                        observer.removeObserver {}
                    }
                    carObserver = null
                }
            } catch (e: Exception) {
                Log.e("CarViewModel", "Error loading user history", e)
                _error.postValue("Error loading history: ${e.message}")
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

    private suspend fun insert(car: CarEntity): Long {
        return repository.insertCar(car)
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
                
                if (response.isSuccessful) {
                    Log.d("CarFetch", "API call successful")
                    response.body()?.let { carData ->
                        Log.d("CarFetch", "Car data received: $carData")
                        val user = userPreferences.getUser()
                        Log.d("CarFetch", "User type: ${user?.userType}")
                        
                        if (user != null) {
                            val carEntity = carData.toEntity(user.email)
                            val carId = insert(carEntity)
                            
                            // Save tickets to database with the returned car ID
                            val tickets = carData.tickets ?: emptyList()
                            if (tickets.isNotEmpty()) {
                                repository.saveTickets(carId.toInt(), tickets)
                            }
                            
                            // Update the car with tickets
                            val car = carEntity.toCar().copy(tickets = tickets)
                            _car.postValue(car)
                            _carWithTickets.value = car
                            
                            // Update car history with the new car data
                            _carHistory.value = _carHistory.value?.toMutableList()?.apply {
                                removeAll { it.licenseNumber == car.licenseNumber }
                                add(0, car)
                                sortByDescending { it.id }
                            } ?: listOf(car)
                        } else {
                            Log.d("CarFetch", "No user logged in")
                            _error.postValue("No user logged in")
                        }
                    } ?: run {
                        Log.d("CarFetch", "Response body is null")
                        _error.postValue("No car found with license: $licensePlate")
                        loadUserAndHistory()
                    }
                } else {
                    Log.d("CarFetch", "API call failed")
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
                _isLoading.value = false
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

    private val _carToDelete = MutableStateFlow<Car?>(null)
    val carToDelete: StateFlow<Car?> = _carToDelete

    fun showDeleteDialogForCar(car: Car) {
        _carToDelete.value = car
    }

    fun confirmDeleteCar() {
        _carToDelete.value?.let {
            deleteACar(it)
        }
        _carToDelete.value = null // reset
    }

    fun cancelDelete() {
        _carToDelete.value = null
    }

    fun setShowDeleteConfirmationDialog(show: Boolean) {
        viewModelScope.launch {
            _showDeleteConfirmationDialog.emit(show)
        }
    }

    fun getCarsByUser(email: String): LiveData<List<CarEntity>> {
        return repository.getCarsByUser(email)
    }
    
    fun updateCar(car: Car) {
        _car.value = car
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
        scanDate = this.scanDate,
        tickets = emptyList() // Tickets will be added from the API response
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