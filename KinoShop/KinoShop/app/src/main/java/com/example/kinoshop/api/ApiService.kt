package com.example.kinoshop.api

import com.example.kinoshop.model.ActorCast
import com.example.kinoshop.model.MovieDetail
import com.example.kinoshop.model.Movies
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface ApiService {

//    @GET("/3/movie/{id}")
//    fun getMovie(@Path("id") id: Long): Call<MovieDetail>

    @GET("/3/movie/{id}")
    suspend fun getMovieCoroutine(@Path("id") id: Long): Response<MovieDetail>

    @GET("/3/movie/{movie_id}/credits")
    suspend fun getActorCastByMovieCoroutine(@Path("movie_id") movieId: Long): Response<ActorCast>

    @GET("/3/discover/movie")
    suspend fun getMoviesCoroutine(@Query("page") page: Int): Response<Movies>

}