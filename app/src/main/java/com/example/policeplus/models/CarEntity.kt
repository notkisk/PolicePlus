package com.example.policeplus.models
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "car_table")
data class CarEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "license_plate") val licenseNumber: String,
    @ColumnInfo(name = "owner_name") val owner: String,
    @ColumnInfo(name = "insurance_start") val insuranceStart: String,
    @ColumnInfo(name = "insurance_end") val insuranceEnd: String,
    @ColumnInfo(name = "inspection_start") val inspectionStart: String,
    @ColumnInfo(name = "inspection_end") val inspectionEnd: String,
    @ColumnInfo(name = "tax_paid") val taxPaid: String,
    @ColumnInfo(name = "stolen_car") val stolenCar: String,
    @ColumnInfo(name = "make_and_model") val makeAndModel: String,
    @ColumnInfo(name = "color") val color: String,
    @ColumnInfo(name = "driver_license") val driverLicense: String,
    @ColumnInfo(name = "address") val address: String,
    @ColumnInfo(name = "scan_date") val scanDate: Long,
    @ColumnInfo(name = "user_email") val userEmail: String // ✅ Added
)
