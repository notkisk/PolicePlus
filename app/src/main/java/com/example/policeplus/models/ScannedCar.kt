import java.text.SimpleDateFormat
import java.util.*

data class ScannedCar(
    val car: Car,
    val scanTimestamp: String = getCurrentTimestamp()
)

// Function to generate the current timestamp
fun getCurrentTimestamp(): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    return dateFormat.format(Date()) // Returns formatted date & time
}
