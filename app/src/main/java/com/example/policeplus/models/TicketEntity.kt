package com.example.policeplus.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tickets",
    foreignKeys = [
        ForeignKey(
            entity = CarEntity::class,
            parentColumns = ["id"],
            childColumns = ["carId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("carId")]
)
data class TicketEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val carId: Int,
    val driverLicense: String,
    val ticketDate: String,
    val ticketType: String,
    val details: String
)

fun TicketEntity.toTicket() = Ticket(
    driverLicense = driverLicense,
    ticketDate = ticketDate,
    ticketType = ticketType,
    details = details
)

fun Ticket.toEntity(carId: Int) = TicketEntity(
    carId = carId,
    driverLicense = driverLicense,
    ticketDate = ticketDate,
    ticketType = ticketType,
    details = details
)
