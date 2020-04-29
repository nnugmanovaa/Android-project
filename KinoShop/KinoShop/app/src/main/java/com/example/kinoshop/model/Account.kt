package com.example.kinoshop.model

import com.google.gson.annotations.SerializedName

data class Account(
    val id: Long,
    @SerializedName("username")
    val name: String
)