package com.example.kinoshop.model

import com.google.gson.annotations.SerializedName


 class Movies{
    @SerializedName("page")
    private val page = 0

    @SerializedName("results")
    private lateinit var results: List<MovieDetail>

    @SerializedName("total_results")
    private val totalResults = 0

    @SerializedName("total_pages")
    val totalPages = 0

    fun getResults(): List<MovieDetail> {
        return results
    }
}
//    @SerializedName("page")
//    val page: Int,
//    @SerializedName("total_pages") val totalPages: Int,
//    val results: List<MovieDetail>
