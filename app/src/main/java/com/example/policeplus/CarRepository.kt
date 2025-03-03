import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.google.crypto.tink.Aead
import com.google.crypto.tink.KeysetHandle
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.aead.AeadKeyTemplates
import com.google.gson.Gson
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking

// Create an Encrypted DataStore
val Context.encryptedDataStore by preferencesDataStore(name = "encrypted_cars")

class CarRepository(private val context: Context) {
    private val CAR_HISTORY_KEY = stringPreferencesKey("car_history")

    // Initialize Tink AEAD Encryption
    private val aead: Aead by lazy {
        AeadConfig.register()
        val keysetHandle = KeysetHandle.generateNew(AeadKeyTemplates.AES256_GCM)
        keysetHandle.getPrimitive(Aead::class.java)
    }

    suspend fun saveCarHistory(carHistory: List<Car>) {
        val encryptedStore = context.encryptedDataStore
        val jsonHistory = Gson().toJson(carHistory) // Convert list to JSON

        // Encrypt JSON
        val encryptedData = aead.encrypt(jsonHistory.toByteArray(), null)
        val encryptedString = encryptedData.joinToString(",") // Convert to String

        encryptedStore.edit { preferences ->
            preferences[CAR_HISTORY_KEY] = encryptedString
        }
    }

    fun loadCarHistory(): Flow<List<Car>> {
        return context.encryptedDataStore.data.map { preferences ->
            val encryptedString = preferences[CAR_HISTORY_KEY] ?: return@map emptyList<Car>()

            try {
                val encryptedData = encryptedString.split(",").map { it.toByte() }.toByteArray()
                val decryptedJson = String(aead.decrypt(encryptedData, null))
                Gson().fromJson(decryptedJson, Array<Car>::class.java).toList()
            } catch (e: Exception) {
                emptyList()
            }
        }
    }
}
