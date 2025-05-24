package com.example.policeplus

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.policeplus.models.TicketEntity

@Dao
interface TicketDao {
    @Query("SELECT * FROM tickets WHERE carId = :carId")
    fun getTicketsForCar(carId: Int): LiveData<List<TicketEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTickets(tickets: List<TicketEntity>)

    @Query("DELETE FROM tickets WHERE carId = :carId")
    suspend fun deleteTicketsForCar(carId: Int)

    @Query("SELECT * FROM tickets WHERE carId = :carId")
    suspend fun getTicketsForCarSync(carId: Int): List<TicketEntity>
}
