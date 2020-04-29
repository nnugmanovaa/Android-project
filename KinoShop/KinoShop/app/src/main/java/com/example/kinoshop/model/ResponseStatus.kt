package com.example.kinoshop.model

import com.google.gson.annotations.SerializedName

data class ResponseStatus(
    @SerializedName("status_code") val statusCode: Int,
    @SerializedName("status_message") val statusMessage: String
)