package com.example.policeplus.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.policeplus.CarDao
import com.example.policeplus.CarDatabase
import com.example.policeplus.CarRepository
import com.example.policeplus.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): CarDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            CarDatabase::class.java,
            "car_db"
        ).fallbackToDestructiveMigration().addMigrations(MIGRATION_2_3).build()


    }

    @Provides
    fun provideCarDao(database: CarDatabase): CarDao {
        return database.carDao()
    }







}


val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE car_table ADD COLUMN owner_name TEXT DEFAULT ''")
        database.execSQL("ALTER TABLE car_table ADD COLUMN insurance_start TEXT DEFAULT ''")
        database.execSQL("ALTER TABLE car_table ADD COLUMN insurance_end TEXT DEFAULT ''")
        database.execSQL("ALTER TABLE car_table ADD COLUMN inspection_start TEXT DEFAULT ''")
        database.execSQL("ALTER TABLE car_table ADD COLUMN inspection_end TEXT DEFAULT ''")
        database.execSQL("ALTER TABLE car_table ADD COLUMN tax_paid TEXT DEFAULT ''")
        database.execSQL("ALTER TABLE car_table ADD COLUMN stolen_car TEXT DEFAULT ''")
        database.execSQL("ALTER TABLE car_table ADD COLUMN make_and_model TEXT DEFAULT ''")
        database.execSQL("ALTER TABLE car_table ADD COLUMN color TEXT DEFAULT ''")
        database.execSQL("ALTER TABLE car_table ADD COLUMN driver_license TEXT DEFAULT ''")
        database.execSQL("ALTER TABLE car_table ADD COLUMN address TEXT DEFAULT ''")
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE car_table ADD COLUMN scanDate INTEGER NOT NULL DEFAULT 0")
    }
}