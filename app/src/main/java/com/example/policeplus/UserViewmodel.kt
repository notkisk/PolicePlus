package com.example.policeplus
import TokenManager
import UserPreferences
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.policeplus.models.NormalUser
import com.example.policeplus.models.User
import com.example.policeplus.UserRegisterRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val repository: UserRepository,
    application: Application
) : AndroidViewModel(application) {

    private val userPreferences = UserPreferences(application)
    private val tokenManager = TokenManager(application)

    private val _token = MutableStateFlow<String?>(null)
    val token: StateFlow<String?> = _token

    // IMPORTANT: Remove profileData and normalProfileData.  We only need localUser.
    // private val _profileData = MutableLiveData<LoginResponse>()
    // val profileData: LiveData<LoginResponse> get() = _profileData

    // private val _normalProfileData = MutableLiveData<NormalLoginResponse>()
    // val normalProfileData: LiveData<NormalLoginResponse> get() = _normalProfileData

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
                    val user = User(
                        id = 0, // Or get from response
                        email = loginResponse.user.email,
                        password = "", // Do NOT store
                        name = loginResponse.user.name,
                        licenseNumber = loginResponse.user.licenseNumber,
                        rank = "",
                        department = "", // Or default
                        badgeNumber = "",
                        carsScanned = 0,  // Or default
                        officerImage = "", // Or default
                        userType = "normal", // IMPORTANT: Set userType
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
            _localUser.postValue(user) // Use postValue for background thread updates
        }
    }

    private fun saveToken(token: String) {
        viewModelScope.launch {
            tokenManager.saveToken(token)
            _token.value = token
        }
    }

    fun logout() {
        viewModelScope.launch {
            tokenManager.clearToken()
            userPreferences.clearUser()
            _token.value = null
            _localUser.value = null
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
}
