import RetrofitInstance.api
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

// Define DataStore filename
private const val CAR_HISTORY_FILE = "car_history.json"

// Serializer for CarHistory
object CarHistorySerializer : Serializer<List<Car>> {
    override val defaultValue: List<Car> = emptyList()

    override suspend fun readFrom(input: InputStream): List<Car> {
        return try {
            Json.decodeFromString<List<Car>>(input.readBytes().decodeToString())
        } catch (e: IOException) {
            emptyList()
        }
    }

    override suspend fun writeTo(t: List<Car>, output: OutputStream) {
        output.write(Json.encodeToString(t).encodeToByteArray())
    }
}

// Create DataStore
private val Application.carHistoryDataStore: DataStore<List<Car>> by dataStore(
    fileName = CAR_HISTORY_FILE,
    serializer = CarHistorySerializer
)

class CarViewModel(application: Application) : AndroidViewModel(application) {
    private val carRepo = CarRepository(application)

    private val _car = MutableStateFlow<Car?>(null)
    val car: StateFlow<Car?> = _car

    private val _carHistory = MutableStateFlow<List<Car>>(emptyList())
    val carHistory: StateFlow<List<Car>> = _carHistory

    val latestScans: StateFlow<List<Car>> = _carHistory
        .map { it.take(2) }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        viewModelScope.launch {
            carRepo.loadCarHistory().collect { history ->
                _carHistory.value = history
            }
        }
    }

    fun fetchCar(plate: String) {
        Log.d("CarViewModel", "fetchCar() called with plate: $plate")

        viewModelScope.launch {
            try {
                val response = api.getCarByPlate(plate)
                if (response.isSuccessful) {
                    response.body()?.let { fetchedCar ->
                        _car.value = fetchedCar

                        val updatedHistory = listOf(fetchedCar) + _carHistory.value
                        _carHistory.value = updatedHistory

                        carRepo.saveCarHistory(updatedHistory) // Save encrypted history
                    }
                } else {
                    _errorMessage.value = "Failed to fetch car data"
                }
            } catch (e: Exception) {
                Log.e("CarViewModel", "Error fetching car", e)
                _errorMessage.value = "An error occurred"
            }
        }
    }
}


