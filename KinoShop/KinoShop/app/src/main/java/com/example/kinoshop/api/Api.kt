package com.example.kinoshop.api

import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


const val BASE_URL = "https://api.themoviedb.org/"
const val API_KEY = "2175a1bb0362004e9e1dca79f8b7dc6f"

class Api {

    fun serviceInitialize(): ApiService {
        val keyInterceptor = initAuthKeyInterceptor()
        val okHttpClient = initOkHttp(keyInterceptor)
        return initRetrofitService(okHttpClient)
    }

    private fun initAuthKeyInterceptor() = object : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val original: Request = chain.request()
            val originalHttpUrl: HttpUrl = original.url

            val url = originalHttpUrl.newBuilder()
                .addQueryParameter("api_key", API_KEY)
                .build()

            val requestBuilder: Request.Builder = original.newBuilder()
                .url(url)

            val request: Request = requestBuilder.build()
            return chain.proceed(request)
        }
    }

    private fun initOkHttp(keyInterceptor: Interceptor): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(keyInterceptor)
        .connectTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    private fun initRetrofitService(okHttpClient: OkHttpClient): ApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(ApiService::class.java)
    }
}