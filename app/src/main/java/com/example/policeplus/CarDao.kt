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
    @Query("SELECT * FROM car_table WHERE user_email = :email ORDER BY id DESC")
    fun getAllCarsByUser(email: String): LiveData<List<CarEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCar(car: CarEntity)

    @Query("SELECT * FROM car_table WHERE license_plate = :license AND user_email = :userEmail")
    fun getCarByLicense(license: String, userEmail: String): LiveData<CarEntity?>

    @Query("DELETE FROM car_table WHERE id = :id")
    suspend fun deleteCar(id: String)

    @Query("DELETE FROM car_table WHERE user_email = :email")
    suspend fun deleteAllUserCars(email: String)
}
