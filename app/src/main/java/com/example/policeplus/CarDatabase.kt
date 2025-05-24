package com.example.policeplus

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.policeplus.migrations.MIGRATION_3_4
import com.example.policeplus.models.CarEntity
import com.example.policeplus.models.TicketEntity

@Database(
    entities = [CarEntity::class, TicketEntity::class], 
    version = 4, 
    exportSchema = true
)
abstract class CarDatabase: RoomDatabase() {
    abstract fun carDao(): CarDao
    abstract fun ticketDao(): TicketDao

    companion object {
        @Volatile
        private var INSTANCE: CarDatabase? = null

        fun getDatabase(context: Context): CarDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CarDatabase::class.java,
                    "car_database"
                )
                .addMigrations(MIGRATION_3_4)
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
