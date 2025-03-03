import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface CarApiService {
    @GET("/cars") // Make sure this matches your Node.js route
    fun getCars(): Call<List<Car>>

    @GET("cars/{plate}")
    suspend fun getCarByPlate(@Path("plate") plate: String): Response<Car>
}
