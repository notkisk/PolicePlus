package com.example.policeplus

import retrofit2.Response
import RetrofitInstance.api
import com.example.policeplus.UserRegisterRequest
import javax.inject.Singleton

@Singleton
class UserRepository  {
    suspend fun registerUser(user: RegisterRequest): Response<RegisterResponse> {
        return  api.register(user)
    }
    suspend fun registerUserNormal(user: UserRegisterRequest): Response<RegisterResponse> {
        return  api.normalRegister(user)
    }

    suspend fun loginUser(email: String, password: String): Response<LoginResponse> {
        return api.login(LoginRequest(email, password))
    }
    suspend fun normalLoginUser(email: String, password: String): Response<NormalLoginResponse> {
        return api.normalLogin(LoginRequest(email, password))
    }


}
