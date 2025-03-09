import com.example.policeplus.CarApiService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "https://policeplus-api.onrender.com"


    // Token should be stored securely (e.g., in DataStore or SharedPreferences)
    var authToken: String? = null

    private val authInterceptor = Interceptor { chain ->
        val request: Request = chain.request().newBuilder().apply {
            authToken?.let {
                addHeader("Authorization", "Bearer $it")  // Attach token to headers
            }
        }.build()
        chain.proceed(request)
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)  // Attach interceptor
        .build()

    val api: CarApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)  // Use custom OkHttpClient
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CarApiService::class.java)
    }
}
