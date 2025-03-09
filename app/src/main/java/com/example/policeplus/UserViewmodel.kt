package com.example.policeplus
import TokenManager
import UserPreferences
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.policeplus.models.User
import com.google.common.base.Strings.isNullOrEmpty
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val repository: UserRepository,
    application: Application
) : AndroidViewModel(application) {

    private val userPreferences = UserPreferences(application) // ✅ Initialize manually

    private val tokenManager = TokenManager(application)

    private val _token = MutableStateFlow<String?>(null)
    val token: StateFlow<String?> = _token

    private val _profileData = MutableLiveData<LoginResponse>()
    val profileData: LiveData<LoginResponse> get() = _profileData

    private val _localUser = MutableLiveData<User?>()
    val localUser: LiveData<User?> get() = _localUser

    init {
        viewModelScope.launch {
            tokenManager.token.collect { savedToken ->
                _token.value = savedToken
                RetrofitInstance.authToken = savedToken
            }
        }
        fetchLocalUser() // Now runs outside the coroutine block, ensuring faster execution
    }


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

    fun loginUser(loginRequest: LoginRequest, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = repository.loginUser(loginRequest.email, loginRequest.password)
                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()!!

                    _profileData.value = loginResponse

                    val user = User(
                        id = 0,  // Default value
                        email = loginResponse.user.email,
                        password = "",  // Default value
                        name = loginResponse.user.name,
                        badgeNumber = loginResponse.user.badgeNumber,
                        officerImage = loginResponse.user.officerImage ?: "",
                        rank = loginResponse.user.rank ?: "",
                        department = loginResponse.user.department ?: "",
                        carsScanned = loginResponse.user.carsScanned,
                    )

                    saveUserLocally(user)
                    _localUser.value = user

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
            _localUser.postValue(user) // Ensure UI updates properly
        }
    }


    fun refreshLocalUser() {
        viewModelScope.launch { fetchLocalUser() }
    }

    private fun saveUserLocally(user: User) {
        viewModelScope.launch {
            userPreferences.saveUser(user)
            _localUser.value = user
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
}

