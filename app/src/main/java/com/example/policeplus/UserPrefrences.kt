import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.policeplus.models.Officer
import com.example.policeplus.models.User
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "user_prefs")
class UserPreferences(private val context: Context) {

    companion object {
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val USER_BADGE = stringPreferencesKey("user_badge")
        val USER_IMAGE = stringPreferencesKey("user_image")
        val USER_RANK = stringPreferencesKey("user_rank")
        val USER_DEPARTMENT = stringPreferencesKey("user_department")
        val USER_SCANNED_CARS = intPreferencesKey("user_scanned_cars")
        val USER_TYPE = stringPreferencesKey("user_type") // "police" or "normal"
        val USER_LICENSE_NUMBER = stringPreferencesKey("user_license_number") // "police" or "normal"

    }

    suspend fun saveUser(user: User) {
        context.dataStore.edit { prefs ->
            prefs[USER_NAME] = user.name
            prefs[USER_EMAIL] = user.email
            prefs[USER_TYPE] = user.userType // You'll add this field in your User class
            prefs[USER_BADGE] = user.badgeNumber ?: ""
            prefs[USER_IMAGE] = user.officerImage ?: ""
            prefs[USER_RANK] = user.rank ?: ""
            prefs[USER_DEPARTMENT] = user.department ?: ""
            prefs[USER_SCANNED_CARS] = user.carsScanned ?: 0
            prefs[USER_LICENSE_NUMBER] = (user.licenseNumber ?: 0).toString()
        }
    }


    suspend fun getUser(): User? {
        val prefs = context.dataStore.data.first()
        val name = prefs[USER_NAME] ?: return null
        val email = prefs[USER_EMAIL] ?: ""
        val userType = prefs[USER_TYPE] ?: "police" // fallback to police if old data

        return User(
            id = 0,
            password = "",
            name = name,
            email = email,
            userType = userType,
            badgeNumber = prefs[USER_BADGE] ?: "",
            officerImage = prefs[USER_IMAGE] ?: "",
            rank = prefs[USER_RANK] ?: "",
            department = prefs[USER_DEPARTMENT] ?: "",
            carsScanned = prefs[USER_SCANNED_CARS] ?: 0,
            licenseNumber = (prefs[USER_LICENSE_NUMBER] ?: 0).toString(),
        )
    }


    suspend fun clearUser() {
        context.dataStore.edit { it.clear() }
    }
}