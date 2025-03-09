package com.example.policeplus

import RetrofitInstance.api
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.example.policeplus.models.CarEntity
import com.example.policeplus.CarRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.app.Application
import com.example.policeplus.models.Car

@HiltViewModel
class CarViewModel @Inject constructor(
    private val repository: CarRepository
) : ViewModel() {

    private val _car = MutableLiveData<Car?>(null)
    val car: LiveData<Car?> = _car

    private val _carHistory = MutableStateFlow<List<Car>>(emptyList())
    val carHistory: StateFlow<List<Car>> = _carHistory

    private val _isLoading = MutableLiveData<Boolean> (false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>("")
    val error: LiveData<String> = _error

    val allCars: LiveData<List<CarEntity>> = repository.allCars

    private fun insert(car: CarEntity) = viewModelScope.launch {
        repository.insertCar(car)
    }

    init {
        // Observe the Room database and update carHistory
        viewModelScope.launch {
            repository.allCars.asFlow().collect { carEntities ->
                _carHistory.value = carEntities.map { it.toCar() }
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
                        insert(carData.toEntity())
                    }
                } else {
                    _error.postValue(response.message())
                    _car.postValue(null)
                }
            } catch (e: Exception) {
                _car.postValue(null)
            }finally {
                _isLoading.postValue(false)

            }
        }
    }
}


fun Car.toEntity(): CarEntity {
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
        scanDate = System.currentTimeMillis()
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



