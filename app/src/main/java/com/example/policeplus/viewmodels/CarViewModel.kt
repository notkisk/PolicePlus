import RetrofitInstance.api
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch




class CarViewModel(application: Application) : AndroidViewModel(application) {
    private val carRepo = CarRepository(application)

    private val _car = MutableLiveData<Car?>(null)
    val car: LiveData<Car?> = _car

    private val _carHistory = MutableStateFlow<List<Car>>(emptyList())
    val carHistory: StateFlow<List<Car>> = _carHistory

    val latestScans: StateFlow<List<Car>> = _carHistory
        .map { it.take(2) } // ✅ Take the most recent 2 cars
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



    fun fetchCar(licensePlate: String) {
        viewModelScope.launch {
            try {
                val response = api.getCarByPlate(licensePlate)
                if (response.isSuccessful) {
                    response.body()?.let { carData ->
                        _car.postValue(carData) // ✅ Updating LiveData

                        // ✅ Add new car to history
                        _carHistory.value = listOf(carData) + _carHistory.value
                    }
                } else {
                    _car.postValue(null)
                }
            } catch (e: Exception) {
                _car.postValue(null)
            }
        }
    }


}


