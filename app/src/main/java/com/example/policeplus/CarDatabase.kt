package com.example.policeplus

import androidx.room.Database
import androidx.room.RoomDatabase

import com.example.policeplus.models.CarEntity
import com.example.policeplus.CarDao

@Database(entities = [CarEntity::class], version = 4, exportSchema = true)
abstract class CarDatabase: RoomDatabase() {
    abstract fun carDao(): CarDao

}




