package com.example.policeplus

import android.app.Application
import androidx.work.*
import dagger.hilt.android.HiltAndroidApp
import com.example.policeplus.utils.NotificationHelper
import com.example.policeplus.workers.CarStatusCheckWorker
import java.util.concurrent.TimeUnit

@HiltAndroidApp
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createNotificationChannel(this)
        setupPeriodicWorkManager()
    }

    private fun setupPeriodicWorkManager() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val periodicWorkRequest = PeriodicWorkRequestBuilder<CarStatusCheckWorker>(
            15, TimeUnit.MINUTES, // Check every 15 minutes
            5, TimeUnit.MINUTES  // Flex interval
        )
        .setConstraints(constraints)
        .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "car_status_check",
            ExistingPeriodicWorkPolicy.KEEP, // Keep existing if any
            periodicWorkRequest
        )
    }
}
