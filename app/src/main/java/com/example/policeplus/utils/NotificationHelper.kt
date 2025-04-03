package com.example.policeplus.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.policeplus.R
import java.time.LocalDate
import java.time.temporal.ChronoUnit

object NotificationHelper {
    private const val CHANNEL_ID = "expiration_notifications"
    private const val INSURANCE_NOTIFICATION_ID = 1
    private const val INSPECTION_NOTIFICATION_ID = 2

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Expiration Notifications"
            val descriptionText = "Notifications for insurance and inspection expiration"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun checkAndNotifyExpirations(
        context: Context,
        insuranceExpireDate: LocalDate?,
        inspectionExpireDate: LocalDate?,
        carLicensePlate: String
    ) {
        insuranceExpireDate?.let { checkExpiration(context, it, "Insurance", INSURANCE_NOTIFICATION_ID, carLicensePlate) }
        inspectionExpireDate?.let { checkExpiration(context, it, "Inspection", INSPECTION_NOTIFICATION_ID, carLicensePlate) }
    }

    private fun checkExpiration(context: Context, expirationDate: LocalDate, type: String, notificationId: Int, carLicensePlate: String) {
        val today = LocalDate.now()
        val daysUntilExpiration = ChronoUnit.DAYS.between(today, expirationDate)

        when {
            daysUntilExpiration == 7L -> showNotification(
                context,
                "$type Expiration Warning - $carLicensePlate",
                "Your $type for car $carLicensePlate will expire in 7 days",
                notificationId
            )
            daysUntilExpiration <= 0L -> showNotification(
                context,
                "$type Expired - $carLicensePlate",
                if (daysUntilExpiration == 0L)
                    "Your $type for car $carLicensePlate has expired today"
                else
                    "Your $type for car $carLicensePlate has expired ${-daysUntilExpiration} days ago",
                notificationId
            )
        }
    }

    fun showNotification(context: Context, title: String, content: String, notificationId: Int) {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification) // Make sure to add this icon
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, builder.build())
    }

    // For testing notifications
    fun testNotification(context: Context) {
        val today = LocalDate.now()
        val sevenDaysFromNow = today.plusDays(7)
        
        // Test 7-day warning notification
        showNotification(
            context,
            "Insurance Expiration Warning - TEST",
            "Your insurance for car TEST123 will expire in 7 days",
            100 // Using a different ID for test notification
        )
        
        // Test expiration notification
        showNotification(
            context,
            "Insurance Expired - TEST",
            "Your insurance for car TEST123 has expired today",
            101 // Using a different ID for test notification
        )
    }
}
