package com.example.policeplus

import RetrofitInstance.api
import UserPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.policeplus.models.CarEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.policeplus.models.Car

@HiltViewModel
class CarViewModel @Inject constructor(
    application: Application, // <- add this
    private val repository: CarRepository
) : AndroidViewModel(application) {

    private val userPreferences = UserPreferences(application)


    private val _car = MutableLiveData<Car?>(null)
    val car: LiveData<Car?> = _car

    private val _carHistory = MutableStateFlow<List<Car>>(emptyList())
    val carHistory: StateFlow<List<Car>> = _carHistory

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData("")
    val error: LiveData<String> = _error

    private var userEmail: String = ""

    private var _allCars = MutableLiveData<List<CarEntity>>()
    val allCars: LiveData<List<CarEntity>> get() = _allCars

    init {
        loadUserAndHistory()
    }

     fun loadUserAndHistory() {
        viewModelScope.launch {
            val user = userPreferences.getUser()
            user?.let { it ->
                userEmail = it.email
                repository.getCarsByUser(userEmail).observeForever { cars ->
                    _allCars.value = cars
                    _carHistory.value = cars.map { it.toCar() }
                }
            }
        }
    }

    val latestScans: StateFlow<List<Car>> = _carHistory
        .map { it.take(2) }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun fetchCar(licensePlate: String) {
        viewModelScope.launch {
            _isLoading.postValue(true)
            _error.postValue("")
            try {
                val response = api.getCarByPlate(licensePlate)
                delay(2000)
                if (response.isSuccessful) {
                    response.body()?.let { carData ->
                        _car.postValue(carData)
                        insert(carData.toEntity(userEmail)) // ✅ Save with correct user
                    }
                } else {
                    _error.postValue(response.message())
                    _car.postValue(null)
                }
            } catch (e: Exception) {
                _car.postValue(null)
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    private fun insert(car: CarEntity) = viewModelScope.launch {
        repository.insertCar(car)
    }

    fun deleteACar(carToDelete:Car){
        viewModelScope.launch {
            _carHistory.value = _carHistory.value.filter { it.scanDate != carToDelete.scanDate }
            repository.deleteCar(carToDelete.scanDate.toString())
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
        userEmail = userEmail
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



