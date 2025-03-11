package com.example.policeplus

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.policeplus.models.CarEntity


@Dao
interface CarDao {

    @Query("SELECT * FROM car_table ORDER BY id DESC")
    fun getAllCars(): LiveData<List<CarEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCar(car: CarEntity)

    @Query("SELECT * FROM car_table WHERE user_email = :email ORDER BY id DESC")
    fun getAllCarsByUser(email: String): LiveData<List<CarEntity>>

    @Query("SELECT * FROM car_table WHERE license_plate = :license")
    fun getCarByLicense(license: String): LiveData<CarEntity?>

    @Query("DELETE FROM car_table WHERE scan_date = :scanDate")
    suspend fun deleteCar(scanDate: String)

    @Query("DELETE FROM car_table")
    suspend fun deleteAllCars()
}
