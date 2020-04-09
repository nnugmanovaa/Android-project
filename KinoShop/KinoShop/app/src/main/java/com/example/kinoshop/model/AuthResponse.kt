package com.example.kinoshop.model

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    val success: Boolean = false,
    @SerializedName("request_token") val reqToken: String
)