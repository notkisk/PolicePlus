package com.example.policeplus.models

import com.google.gson.annotations.SerializedName


data class User(
    @SerializedName("id")val id: Int,
    @SerializedName("email")val email: String,
    @SerializedName("password")val password: String,
    @SerializedName("name")val name: String,
    @SerializedName("rank")val rank: String?,
    @SerializedName("department")val department: String?,
    @SerializedName("badge_number")val badgeNumber: String,
    @SerializedName("cars_scanned")val carsScanned: Int,
    @SerializedName("officer_image")val officerImage: String?
)
