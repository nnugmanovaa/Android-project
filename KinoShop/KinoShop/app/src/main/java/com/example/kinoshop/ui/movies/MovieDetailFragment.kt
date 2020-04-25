package com.example.kinoshop.ui.movies

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.kinoshop.MainActivity
import com.example.kinoshop.R
import com.example.kinoshop.api.Api
import com.example.kinoshop.model.*
import kotlinx.android.synthetic.main.fragment_movie_detail.*
import kotlinx.android.synthetic.main.fragment_movies.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.*
import java.lang.Exception

class MovieDetailFragment : Fragment(), CoroutineScope {

    private val job = Job()

    private var movieDetailDao: MovieDetailDao? = null

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    private lateinit var mainActivity: MainActivity
    private var movieId = 0L
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_movie_detail, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainActivity = activity as MainActivity
        arguments?.let {
            val safeArgs = MovieDetailFragmentArgs.fromBundle(it)
            movieId = safeArgs.movieId
        }
        fab.setOnClickListener {
            if (mainActivity.isSignIn) {
                markFavoriteMedia()
            } else {
                Toast.makeText(context, getString(R.string.req_auth), Toast.LENGTH_SHORT).show()
            }
        }

        val api = Api()
        val apiService = api.serviceInitialize()


        movieDetailDao = MovieDetailDatabase.getDatabase(context = activity as Context).movieDetailDao()
        launch{
            val list = withContext(Dispatchers.IO){
                try{
                    val response = apiService.getMovieCoroutine(movieId)
                    if(response.isSuccessful){
                        val result = response.body()
                        if (result!=null){

                        }
                        result
                    }
                    else{
                        movieDetailDao?.getMovieById(movieId)
                    }
                } catch (e: Exception){
                    movieDetailDao?.getMovieById(movieId)
                }
            }
            collapsingToolbar.title = list?.title
            context?.let {
                Glide.with(it)
                    .load("${getString(R.string.download_image_url)}${list?.posterPath}")
                    .placeholder(R.drawable.ic_movies)
                    .centerCrop()
                    .into(posterImage)
            }
            overviewContent.text = list?.overview
        }




//////
//        launch{
//            val response = apiService.getMovieCoroutine(movieId)
//            if (response.isSuccessful){
//                val movie = response.body()
//                collapsingToolbar.title = movie?.title
//                context?.let {
//                    Glide.with(it)
//                        .load("${getString(R.string.download_image_url)}${movie?.posterPath}")
//                        .placeholder(R.drawable.ic_movies)
//                        .centerCrop()
//                        .into(posterImage)
//                }
//                overviewContent.text = movie?.overview
////                val genres = movie?.genres?.map { it.name }
////                genreContent.text = genres?.joinToString(", ")
//            }
//
//        }

        launch{
            val response = apiService.getActorCastByMovieCoroutine(movieId)
            if (response.isSuccessful){
                val actorCast = response.body()
                val actors = actorCast?.cast?.map { it.name }
                actorCastContent.text = actors?.joinToString(", ")
            }
        }
    }

    private fun markFavoriteMedia() {
        val api = Api()
        val apiService = api.serviceInitialize()
        mainActivity.account?.id?.let {
            launch{
                val response = apiService.markFavoriteMovieCoroutine(
                    accountId = it,
                    sessionId = mainActivity.sessionId,
                    mediaType = "movie",
                    mediaId = movieId,
                    favorite = true
                )
                if (response.isSuccessful){
                    if (response.body() != null) {
                        context?.let {
                            Toast.makeText(
                                it,
                                getString(R.string.movie_added_favorite),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    } else {
                        context?.let {
                            Toast.makeText(
                                it,
                                getString(R.string.check_auth_data),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

    private fun showToastCheckNetwork() {
        context?.let {
            Toast.makeText(
                it,
                getString(R.string.check_network_connection),
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}