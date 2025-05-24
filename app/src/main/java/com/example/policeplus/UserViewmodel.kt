package com.example.policeplus
import TokenManager
import UserPreferences
import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.policeplus.models.NormalUser
import com.example.policeplus.models.User
import com.example.policeplus.UserRegisterRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.policeplus.utils.NotificationHelper
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class UserViewModel @Inject constructor(
    private val repository: UserRepository,
    private val carManager: CarManager,
    private val carRepository: CarRepository,
    application: Application
) : AndroidViewModel(application) {

    private val userPreferences = UserPreferences(application)
    private val tokenManager = TokenManager(application)
    private val _token = MutableStateFlow<String?>(null)
    val token: StateFlow<String?> = _token
    private val _driverLicense = MutableLiveData<String>()
    val driverLicense: LiveData<String> get() = _driverLicense
    // IMPORTANT: Remove profileData and normalProfileData.  We only need localUser.
    // private val _profileData = MutableLiveData<LoginResponse>()
    // val profileData: LiveData<LoginResponse> get() = _profileData

     private val _normalProfileData = MutableLiveData<NormalLoginResponse>()
     val normalProfileData: LiveData<NormalLoginResponse> get() = _normalProfileData

    private val _localUser = MutableLiveData<User?>() // Holds ALL user data (police or normal)
    val localUser: LiveData<User?> get() = _localUser

    init {
        viewModelScope.launch {
            tokenManager.token.collect { savedToken ->
                _token.value = savedToken
                RetrofitInstance.authToken = savedToken
            }
        }
        fetchLocalUser()
        startExpirationChecks()
    }

    // ... (registerUser remains unchanged) ...
    fun registerUser(user: RegisterRequest, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = repository.registerUser(user)
                onResult(response.isSuccessful, response.message())
            } catch (e: Exception) {
                onResult(false, "Error: ${e.localizedMessage}")
            }
        }
    }
    // Separate login functions for police and normal users
    fun loginUser(loginRequest: LoginRequest, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = repository.loginUser(loginRequest.email, loginRequest.password)
                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()!!
                    val user = User(
                        id = 0, // Or get from response if available
                        email = loginResponse.user.email,
                        password = "", // You should NOT store the password locally
                        name = loginResponse.user.name,
                        badgeNumber = loginResponse.user.badgeNumber,
                        officerImage = loginResponse.user.officerImage ?: "",
                        rank = loginResponse.user.rank ?: "",
                        department = loginResponse.user.department ?: "",
                        carsScanned = loginResponse.user.carsScanned,
                        userType = "police", // IMPORTANT: Set userType
                        licenseNumber = "", // Police don't have this
                    )

                    saveUserLocally(user)

                    if (loginResponse.token.isNotEmpty()) {
                        RetrofitInstance.authToken = loginResponse.token
                        saveToken(loginResponse.token)
                    }

                    onResult(true, "Login successful!")
                } else {
                    onResult(false, "Login failed: ${response.message()}")
                }
            } catch (e: Exception) {
                onResult(false, "Error: ${e.localizedMessage}")
            }
        }
    }

    fun loginNormal(loginRequest: LoginRequest, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = repository.normalLoginUser(loginRequest.email, loginRequest.password)
                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()!!
                    _driverLicense.postValue(response.headers()["X-Driver-License"])

                    val user = response.body()?.user?.let {
                        User(
                            id = 0, // Or get from response
                            email = loginResponse.user.email,
                            password = "", // Do NOT store
                            name = loginResponse.user.name,
                            licenseNumber = loginResponse.user.licenseNumber,
                            rank = "",
                            department = "", // Or default
                            badgeNumber = "it",
                            carsScanned = 0,  // Or default
                            officerImage = "", // Or default
                            userType = "normal", // IMPORTANT: Set userType
                        )
                    }

                    if (user != null) {
                        saveUserLocally(user)
                        carManager.notifyUserLogin()
                        Log.d("CarFetch", user.toString())
                        try {
                            viewModelScope.launch {
                                user.licenseNumber?.let { license ->
                                    val carViewModel = CarViewModel(getApplication(), carRepository, carManager)
                                    carViewModel.fetchCar(license)
                                }
                            }
                        } catch (e: Exception) {
                            Log.d("fetching after login", "Couldn't fetch car, error: ${e.message}")
                        }
                    }

                    if (loginResponse.token.isNotEmpty()) {
                        RetrofitInstance.authToken = loginResponse.token
                        saveToken(loginResponse.token)
                    }

                    onResult(true, "Login successful!")
                } else {
                    onResult(false, "Login failed: ${response.message()}")
                }
            } catch (e: Exception) {
                onResult(false, "Error: ${e.localizedMessage}")
            }
        }
    }



    private fun fetchLocalUser() {
        viewModelScope.launch {
            val user = userPreferences.getUser()
            _localUser.postValue(user) // Ensure UI updates on the main thread
        }

    }

    fun refreshLocalUser() {
        viewModelScope.launch { fetchLocalUser() }
    }

    private fun saveUserLocally(user: User) {
        viewModelScope.launch {
            userPreferences.saveUser(user)
            _localUser.postValue(user)
            // Notify CarViewModel to load user's data
            viewModelScope.launch {
                carManager.notifyUserLogin()
            }
        }
    }

    private fun saveToken(token: String) {
        viewModelScope.launch {
            tokenManager.saveToken(token)
            _token.value = token
        }
    }

    fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.+[A-Za-z]+"
        return email.matches(emailRegex.toRegex())
    }

    fun logout() {
        viewModelScope.launch {
            userPreferences.clearUser()
            tokenManager.clearToken()
            _localUser.postValue(null)
        }
    }

    fun registerUserAsCitizen(user: UserRegisterRequest, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = repository.registerUserNormal(user)
                onResult(response.isSuccessful, response.message())
            } catch (e: Exception) {
                onResult(false, "Error: ${e.localizedMessage}")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startExpirationChecks() {
        viewModelScope.launch {
            localUser.observeForever { user ->
                if (user?.userType == "normal") {
                    val userEmail = user.email
                    carRepository.getCarsByUser(userEmail).observeForever { cars ->
                        cars?.forEach { car ->
                            val insuranceDate = car.insuranceEnd?.let { 
                                Instant.parse(it).atZone(ZoneId.of("UTC")).toLocalDate()
                            }
                            val inspectionDate = car.inspectionEnd?.let { 
                                Instant.parse(it).atZone(ZoneId.of("UTC")).toLocalDate()
                            }
                            NotificationHelper.checkAndNotifyExpirations(
                                getApplication(),
                                insuranceDate,
                                inspectionDate,
                                car.licenseNumber
                            )
                        }
                    }
                }
            }
        }
    }
}
