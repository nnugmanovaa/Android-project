package com.example.kinoshop.model

import com.google.gson.annotations.SerializedName

data class SessionToken(
    @SerializedName("session_id") val sessionId: String
)