package com.example.policeplus

import androidx.lifecycle.LiveData
import com.example.policeplus.models.CarEntity
import javax.inject.Inject


class CarRepository @Inject constructor(private val carDao: CarDao) {
    suspend fun insertCar(car: CarEntity) {
        carDao.insertCar(car)
    }

    suspend fun deleteCar(car: String) {
        carDao.deleteCar(car)
    }

    suspend fun deleteAllCars(){
        carDao.deleteAllCars()
    }

    fun getCarByLicense(license: String): LiveData<CarEntity?> {
        return carDao.getCarByLicense(license)
    }

    fun getCarsByUser(email: String): LiveData<List<CarEntity>> {
        return carDao.getAllCarsByUser(email)
    }
}

