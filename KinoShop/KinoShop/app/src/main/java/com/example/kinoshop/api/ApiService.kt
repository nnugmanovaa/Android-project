package com.example.kinoshop.api

import com.example.kinoshop.model.*
import retrofit2.Call
import retrofit2.http.*
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface ApiService {

    @GET("/3/movie/{id}")
    suspend fun getMovieCoroutine(@Path("id") id: Long): Response<MovieDetail>

    @GET("/3/movie/{movie_id}/credits")
    suspend fun getActorCastByMovieCoroutine(@Path("movie_id") movieId: Long): Response<ActorCast>

    @GET("/3/discover/movie")
    suspend fun getMoviesCoroutine(@Query("page") page: Int): Response<Movies>

    @GET("/3/authentication/token/new")
    suspend fun getRequestTokenCoroutine(): Response<RequestToken>

    @GET("/3/account")
    suspend fun getAccountCoroutine(@Query("session_id") sessionId: String): Response<Account>

    @GET("/3/authentication/token/validate_with_login")
    suspend fun authorizationCoroutine(
        @Query("username") userName: String,
        @Query("password") password: String,
        @Query("request_token") requestToken: String
    ): Response<AuthResponse>

    @POST("/3/authentication/session/new")
    suspend fun createSessionCoroutine(@Query("request_token") reqToken: String): Response<SessionToken>

    @POST("/3/account/{account_id}/favorite")
    suspend fun markFavoriteMovieCoroutine(
        @Path("account_id") accountId: Long,
        @Query("session_id") sessionId: String,
        @Query("media_type") mediaType: String = "movie",
        @Query("media_id") mediaId: Long,
        @Query("favorite") favorite: Boolean
    ): Response<ResponseStatus>

    @GET("/3/account/{account_id}/favorite/movies")
    suspend fun getFavoriteMovieByAccountIdCoroutine(
        @Path("account_id") accountId: Long,
        @Query("session_id") sessionId: String,
        @Query("page") page: Int
    ): Response<Movies>

    @POST("/3/account/{account_id}/favorite")
    suspend fun deleteFavoriteMovieCoroutine(
        @Path("account_id") accountId: Long,
        @Query("session_id") sessionId: String,
        @Body bodyFavorite: BodyFavorite
    ): Response<ResponseStatus>
}