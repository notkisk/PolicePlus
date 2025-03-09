package com.example.policeplus

import retrofit2.Response
import javax.inject.Inject
import RetrofitInstance.api
import javax.inject.Singleton

@Singleton
class UserRepository  {
    suspend fun registerUser(user: RegisterRequest): Response<RegisterResponse> {
        return  api.register(user)
    }

    suspend fun loginUser(email: String, password: String): Response<LoginResponse> {
        return api.login(LoginRequest(email, password))
    }


}
