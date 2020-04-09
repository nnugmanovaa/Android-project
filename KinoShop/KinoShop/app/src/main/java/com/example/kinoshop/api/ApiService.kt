package com.example.kinoshop.api

import com.example.kinoshop.model.*
import retrofit2.Call
import retrofit2.http.*


interface ApiService {

    @GET("/3/movie/{id}")
    fun getMovie(@Path("id") id: Long): Call<MovieDetail>

    @GET("/3/movie/{movie_id}/credits")
    fun getActorCastByMovie(@Path("movie_id") movieId: Long): Call<ActorCast>

    @GET("/3/discover/movie")
    fun getMovies(@Query("page") page: Int): Call<Movies>

    @GET("/3/authentication/token/new")
    fun getRequestToken(): Call<RequestToken>

    @GET("/3/account")
    fun getAccount(@Query("session_id") sessionId: String): Call<Account>

    @GET("/3/authentication/token/validate_with_login")
    fun authorization(
        @Query("username") userName: String,
        @Query("password") password: String,
        @Query("request_token") requestToken: String
    ): Call<AuthResponse>

    @POST("/3/authentication/session/new")
    fun createSession(@Query("request_token") reqToken: String): Call<SessionToken>

    @POST("/3/account/{account_id}/favorite")
    fun markFavoriteMovie(
        @Path("account_id") accountId: Long,
        @Query("session_id") sessionId: String,
        @Query("media_type") mediaType: String = "movie",
        @Query("media_id") mediaId: Long,
        @Query("favorite") favorite: Boolean
    ): Call<ResponseStatus>

    @GET("/3/account/{account_id}/favorite/movies")
    fun getFavoriteMovieByAccountId(
        @Path("account_id") accountId: Long,
        @Query("session_id") sessionId: String,
        @Query("page") page: Int
    ): Call<Movies>

    @POST("/3/account/{account_id}/favorite")
    fun deleteFavoriteMovie(
        @Path("account_id") accountId: Long,
        @Query("session_id") sessionId: String,
        @Body bodyFavorite: BodyFavorite
    ): Call<ResponseStatus>
}