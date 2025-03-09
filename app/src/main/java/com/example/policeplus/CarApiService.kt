package com.example.policeplus
import com.example.policeplus.models.Car
import com.example.policeplus.models.User
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface CarApiService {
    @GET("/cars") // Make sure this matches your Node.js route
    fun getCars(): Call<List<Car>>

    @GET("cars/{plate}")
    suspend fun getCarByPlate(@Path("plate") plate: String): Response<Car>

    /** 🔵 Register a new user */
    @POST("/register")
    suspend fun register(@Body user: RegisterRequest): Response<RegisterResponse>

    /** 🟢 Login user */
    @POST("/login")
    suspend fun login(@Body credentials: LoginRequest): Response<LoginResponse>


}


/** 🔹 Request Models */
data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String,
    val rank: String?,
    val department: String?,
    val badge_number: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

/** 🔹 Response Models */
data class RegisterResponse(
    val message: String
)

data class LoginResponse(
    val message: String,
    val user: User,
    val token:String
)
