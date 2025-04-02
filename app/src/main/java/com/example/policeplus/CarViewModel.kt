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
                val response = api.getCarByPlate(licensePlate)
                if (response.isSuccessful) {
                    response.body()?.let { carData ->
                        val user = userPreferences.getUser()
                        if (user?.userType == "police") {
                            // Police can view and save any car
                            _car.postValue(carData)
                            insert(carData.toEntity(userEmail))
                        } else {
                            // For normal users, just check if they already have this car
                            _car.postValue(carData)
                            // Check if user already has this car
                            val existingCar = repository.getCarByLicense(licensePlate, user?.email ?: "")
                            if (existingCar.value == null) {
                                insert(carData.toEntity(user?.email ?: ""))
                            }
                        }
                    }
                } else {
                    _error.postValue(response.message())
                    _car.postValue(null)
                }
            } catch (e: Exception) {
                _error.postValue("Error fetching car data: ${e.message}")
                _car.postValue(null)
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
