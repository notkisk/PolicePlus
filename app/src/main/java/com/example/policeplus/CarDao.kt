package com.example.policeplus

import Car
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.policeplus.models.CarEntity


@Dao
interface CarDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCar(car:CarEntity)

    @Query("SELECT * FROM car_table ORDER BY id DESC")
    fun getAllCars(): LiveData<List<CarEntity>>

    @Query("SELECT * FROM car_table WHERE license_plate = :license")
    fun getCarByLicense(license: String): LiveData<CarEntity?>

    @Delete
    suspend fun deleteCar(car:CarEntity)

    @Query("DELETE FROM car_table")
    suspend fun deleteAllCars()




}