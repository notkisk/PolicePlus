import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cars")
data class CarEntity(
    @PrimaryKey val licenseNumber: String,
    val id: Int = 0, // Add ID
    val owner: String?,
    val insuranceStart: String,
    val insuranceEnd: String,
    val inspectionStart: String,
    val inspectionEnd: String,
    val taxPaid: String,
    val stolenCar: String
)
