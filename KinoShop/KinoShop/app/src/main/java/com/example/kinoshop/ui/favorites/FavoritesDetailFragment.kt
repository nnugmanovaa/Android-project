package com.example.kinoshop.ui.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.kinoshop.MainActivity
import com.example.kinoshop.R
import com.example.kinoshop.api.Api
import com.example.kinoshop.model.ActorCast
import com.example.kinoshop.model.BodyFavorite
import com.example.kinoshop.model.MovieDetail
import com.example.kinoshop.model.ResponseStatus
import kotlinx.android.synthetic.main.fragment_favorite_detail.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.CoroutineContext
import kotlinx.android.synthetic.main.fragment_favorite_detail.*

class FavoritesDetailFragment : Fragment(), CoroutineScope {

    private val job = Job()

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
    ): View? = inflater.inflate(R.layout.fragment_favorite_detail, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainActivity = activity as MainActivity
        arguments?.let {
            val safeArgs = FavoritesDetailFragmentArgs.fromBundle(it)
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

        launch{
            val response = apiService.getMovieCoroutine(movieId)
            if (response.isSuccessful){
                val movie = response.body()
                collapsingToolbar.title = movie?.title
                context?.let {
                    Glide.with(it)
                        .load("${getString(R.string.download_image_url)}${movie?.posterPath}")
                        .placeholder(R.drawable.ic_movies)
                        .centerCrop()
                        .into(posterImage)
                }

                overviewContent?.let {
                    it.text = movie?.overview
                }
//                val genres = movie?.genres?.map { it.name }
//                genreContent?.let {
//                    it.text = genres?.joinToString(", ")
//                }
//                val countries = movie?.productionCountries?.map { it.name }
//                countriesContent?.let {
//                    it.text = countries?.joinToString(", ")
//                }
            }

        }

        launch{
            val response = apiService.getActorCastByMovieCoroutine(movieId)
            if (response.isSuccessful){
                val actorCast = response.body()
                val actors = actorCast?.cast?.map { it.name }
                actorCastContent?.let {
                    it.text = actors?.joinToString(", ")
                }
            }
        }

    }

    private fun markFavoriteMedia() {

        val bodyFavorite = BodyFavorite(
            "movie",
            movieId,
            false
        )
        val api = Api()
        val apiService = api.serviceInitialize()

        launch{
            val response = apiService.deleteFavoriteMovieCoroutine(accountId = Long.MAX_VALUE, sessionId = mainActivity.sessionId, bodyFavorite = bodyFavorite )
            if (response.isSuccessful){
                if (response.body() != null) {
                    context?.let {
                        Toast.makeText(
                            it,
                            getString(R.string.movie_remove_favorite),
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