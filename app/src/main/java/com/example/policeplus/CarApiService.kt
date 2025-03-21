package com.example.policeplus
import com.example.policeplus.models.Car
import com.example.policeplus.models.NormalUser
import com.example.policeplus.models.User
import com.example.policeplus.UserRegisterRequest
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface CarApiService {
    @GET("/cars")
    fun getCars(): Call<List<Car>>

    @GET("cars/{plate}")
    suspend fun getCarByPlate(@Path("plate") plate: String): Response<Car>

    @POST("/register")
    suspend fun register(@Body user: RegisterRequest): Response<RegisterResponse>

    @POST("/register/normal")
    suspend fun normalRegister(@Body user: UserRegisterRequest): Response<RegisterResponse>

    @POST("/login")
    suspend fun login(@Body credentials: LoginRequest): Response<LoginResponse>

    @POST("/login")
    suspend fun normalLogin(@Body credentials: LoginRequest): Response<NormalLoginResponse>

    @POST("/ticket")
    suspend fun submitTicket(@Body ticket: Ticket): Response<TicketResponse>

    @POST("/stolen_car/{plate}/{stolen_car}")
    suspend fun reportStolenCar(@Body stolenCarBody: StolenReport,@Path("plate") plate: String, @Path("stolen_car") stolenCar: String): Response<StolenCarResponse>

}


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

data class RegisterResponse(
    val message: String
)

data class LoginResponse(
    val message: String,
    val user: User,
    val token:String
)

data class NormalLoginResponse(
    val message: String,
    val user: NormalUser,
    val token:String
)


data class TicketResponse(
    val message: String,
)

data class StolenCarResponse(
    val message: String,
)


data class Ticket(
    val driver_license: String,
    val ticket_type: String,
    val details: String?,
    val officer_name:String,
    val officer_badge:String

)


data class StolenReport(
    val license_plate: String,
    val stolen_status: String,
    val details: String?,
    val officer_name:String,
    val officer_badge:String

)

data class UserRegisterRequest( val email: String,  val password: String, val name: String, val license_number : String)
