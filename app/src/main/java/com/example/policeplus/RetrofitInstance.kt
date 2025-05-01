import com.example.policeplus.CarApiService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "https://policeplus-api.onrender.com"


    var authToken: String? = null

    private val authInterceptor = Interceptor { chain ->
        val request: Request = chain.request().newBuilder().apply {
            authToken?.let {
                addHeader("Authorization", "Bearer $it")
            }
        }.build()
        chain.proceed(request)
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .build()

    val api: CarApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CarApiService::class.java)
    }
}
