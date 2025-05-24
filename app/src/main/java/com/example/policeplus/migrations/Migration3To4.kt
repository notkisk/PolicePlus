package com.example.policeplus.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS `tickets` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `carId` INTEGER NOT NULL,
                `driverLicense` TEXT NOT NULL,
                `ticketDate` TEXT NOT NULL,
                `ticketType` TEXT NOT NULL,
                `details` TEXT NOT NULL,
                FOREIGN KEY(`carId`) REFERENCES `car_table`(`id`) ON DELETE CASCADE 
            )
        """.trimIndent())
        
        database.execSQL("CREATE INDEX IF NOT EXISTS `index_tickets_carId` ON `tickets` (`carId`)")
    }
}
