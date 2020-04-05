package com.example.kinoshop.api

import com.example.kinoshop.model.ActorCast
import com.example.kinoshop.model.MovieDetail
import com.example.kinoshop.model.Movies
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface ApiService {

    @GET("/3/movie/{id}")
    fun getMovie(@Path("id") id: Long): Call<MovieDetail>

    @GET("/3/movie/{movie_id}/credits")
    fun getActorCastByMovie(@Path("movie_id") movieId: Long): Call<ActorCast>

    @GET("/3/discover/movie")
    fun getMovies(@Query("page") page: Int): Call<Movies>

}