package com.example.kinoshop.model

import com.google.gson.annotations.SerializedName

data class MovieDetail(
    val id: Long,
    val title: String,
    val overview: String,
    val genres: List<Genre>,
    @SerializedName("poster_path")
    val posterPath: String,
    @SerializedName("release_date")
    val releaseDate: String,
    @SerializedName("production_countries")
    val productionCountries: List<Country>
)