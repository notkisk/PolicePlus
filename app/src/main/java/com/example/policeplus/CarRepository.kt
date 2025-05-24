package com.example.policeplus

import androidx.lifecycle.LiveData
import com.example.policeplus.models.CarEntity
import com.example.policeplus.models.TicketEntity
import com.example.policeplus.models.Ticket
import com.example.policeplus.models.toEntity
import com.example.policeplus.models.toTicket
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class CarRepository @Inject constructor(
    private val carDao: CarDao,
    private val ticketDao: TicketDao
) {
    suspend fun insertCar(car: CarEntity): Long {
        return carDao.insertCar(car)
    }

    suspend fun deleteCar(id: String, email: String) {
        carDao.deleteCar(id)
    }

    suspend fun deleteAllUserCars(email: String) {
        carDao.deleteAllUserCars(email)
    }

    fun getCarByLicense(license: String, userEmail: String): LiveData<CarEntity?> {
        return carDao.getCarByLicense(license, userEmail)
    }

    fun getCarsByUser(email: String): LiveData<List<CarEntity>> {
        return carDao.getAllCarsByUser(email)
    }

    suspend fun saveTickets(carId: Int, tickets: List<Ticket>) {
        val ticketEntities = tickets.map { it.toEntity(carId) }
        ticketDao.deleteTicketsForCar(carId)
        if (ticketEntities.isNotEmpty()) {
            ticketDao.insertTickets(ticketEntities)
        }
    }

    fun getTicketsForCar(carId: Int): LiveData<List<TicketEntity>> {
        return ticketDao.getTicketsForCar(carId)
    }

    suspend fun getTicketsForCarSync(carId: Int): List<Ticket> {
        return ticketDao.getTicketsForCarSync(carId).map { it.toTicket() }
    }
}
