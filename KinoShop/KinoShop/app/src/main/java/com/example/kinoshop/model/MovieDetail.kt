package com.example.kinoshop.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "movie_table")
data class MovieDetail(
    @PrimaryKey
    val id: Long,
    val title: String,
    val overview: String,
//    @Ignore
//    val genres: List<Genre>,
//    val genresList: String,
    @SerializedName("poster_path")
    val posterPath: String,
    @SerializedName("release_date")
    val releaseDate: String
//    @SerializedName("production_countries")
//    val productionCountries: List<Country>
)