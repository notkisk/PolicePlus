import com.google.gson.annotations.SerializedName

data class Car(
    @SerializedName("id") val id: Int,
    @SerializedName("license_plate") val licenseNumber: String,
    @SerializedName("owner_name") val owner: String,
    @SerializedName("insurance_start") val insuranceStart: String,
    @SerializedName("insurance_end") val insuranceEnd: String,
    @SerializedName("inspection_start") val inspectionStart: String,
    @SerializedName("inspection_end") val inspectionEnd: String,
    @SerializedName("tax_paid") val taxPaid: String,
    @SerializedName("stolen_car") val stolenCar: String,
    @SerializedName("make_and_model") val makeAndModel: String,
    @SerializedName("color") val color: String,
    @SerializedName("driver_license") val driverLicense: String,
    @SerializedName("address") val address: String,
    val scanDate: Long


)
