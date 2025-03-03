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
    @SerializedName("stolen_car") val stolen_car: String


    )
