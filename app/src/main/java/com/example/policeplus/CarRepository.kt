package com.example.policeplus

import androidx.lifecycle.LiveData
import com.example.policeplus.models.CarEntity
import javax.inject.Inject

class CarRepository @Inject constructor(private val carDao: CarDao) {
    suspend fun insertCar(car: CarEntity) {
        carDao.insertCar(car)
    }

    suspend fun deleteCar(id: String, email: String) {
        carDao.deleteCar(id)
    }

    suspend fun deleteAllUserCars(email: String) {
        carDao.deleteAllUserCars(email)
    }

    fun getCarByLicense(license: String, userEmail: String): LiveData<CarEntity?> {
        return carDao.getCarByLicense(license, userEmail)
    }

    fun getCarsByUser(email: String): LiveData<List<CarEntity>> {
        return carDao.getAllCarsByUser(email)
    }
}
