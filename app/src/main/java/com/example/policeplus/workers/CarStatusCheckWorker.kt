package com.example.policeplus.workers

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.policeplus.CarRepository
import com.example.policeplus.utils.NotificationHelper
import com.example.policeplus.models.CarEntity
import com.example.policeplus.settings.SettingsDataStore
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import androidx.lifecycle.Observer
import kotlinx.coroutines.flow.first

@HiltWorker
class CarStatusCheckWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val carRepository: CarRepository
) : CoroutineWorker(appContext, workerParams) {

    private val settingsDataStore = SettingsDataStore(appContext)

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun doWork(): Result {
        try {
            // First check if notifications are enabled
            val notificationsEnabled = settingsDataStore.notificationsEnabled.first()
            if (!notificationsEnabled) {
                return Result.success()
            }

            // Since we can't directly observe LiveData in a worker, we'll use a temporary observer
            carRepository.getCarsByUser("normal@email.com").observeForever(object : Observer<List<CarEntity>> {
                override fun onChanged(cars: List<CarEntity>) {
                    cars.forEach { car ->
                        // Check insurance and inspection
                        val insuranceDate = car.insuranceEnd.let {
                            Instant.parse(it).atZone(ZoneId.of("UTC")).toLocalDate()
                        }
                        val inspectionDate = car.inspectionEnd.let {
                            Instant.parse(it).atZone(ZoneId.of("UTC")).toLocalDate()
                        }
                        NotificationHelper.checkAndNotifyExpirations(
                            applicationContext,
                            insuranceDate,
                            inspectionDate,
                            car.licenseNumber
                        )

                        // Check tax status
                        if (car.taxPaid == "Not Paid") {
                            NotificationHelper.showNotification(
                                applicationContext,
                                "Car Tax Not Paid - ${car.licenseNumber}",
                                "Your car tax for ${car.licenseNumber} is not paid",
                                car.licenseNumber.hashCode() + 200 // Unique ID for tax notifications
                            )
                        }

                        // Check if stolen
                        if (car.stolenCar == "Yes") {
                            NotificationHelper.showNotification(
                                applicationContext,
                                "⚠️ Car Stolen Alert - ${car.licenseNumber}",
                                "Your car ${car.licenseNumber} has been marked as stolen!",
                                car.licenseNumber.hashCode() + 300 // Unique ID for stolen notifications
                            )
                        }
                    }
                }
            })
            return Result.success()
        } catch (e: Exception) {
            return Result.failure()
        }
    }
}