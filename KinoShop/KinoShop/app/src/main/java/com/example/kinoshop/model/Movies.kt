package com.example.kinoshop.model

import com.google.gson.annotations.SerializedName

data class Movies(
    val page: Int,
    @SerializedName("total_pages") val totalPages: Int,
    val results: List<MovieDetail>
)