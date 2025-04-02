package com.example.policeplus

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CarManager @Inject constructor(
    private val repository: CarRepository
) {
    private val _loginEvent = MutableSharedFlow<Unit>()
    val loginEvent: SharedFlow<Unit> = _loginEvent

    suspend fun clearUserCars(email: String) {
        repository.deleteAllUserCars(email)
    }

    suspend fun notifyUserLogin() {
        _loginEvent.emit(Unit)
    }
}
