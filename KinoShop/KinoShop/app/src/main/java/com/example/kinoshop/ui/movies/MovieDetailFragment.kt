package com.example.kinoshop.ui.movies

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.kinoshop.MainActivity
import com.example.kinoshop.R
import com.example.kinoshop.model.ActorCast
import com.example.kinoshop.model.MovieDetail
import com.example.kinoshop.model.ResponseStatus
import kotlinx.android.synthetic.main.fragment_movie_detail.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MovieDetailFragment : Fragment() {

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
        mainActivity.apiService.getMovie(movieId).enqueue(object : Callback<MovieDetail> {
            override fun onFailure(call: Call<MovieDetail>, t: Throwable) {
                showToastCheckNetwork()
            }

            override fun onResponse(call: Call<MovieDetail>, response: Response<MovieDetail>) {
                val movie = response.body()
                collapsingToolbar?.let {
                    it.title = movie?.title
                }
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
                val genres = movie?.genres?.map { it.name }
                genreContent?.let {
                    it.text = genres?.joinToString(", ")
                }
                val countries = movie?.productionCountries?.map { it.name }
                countriesContent?.let {
                    it.text = countries?.joinToString(", ")
                }
            }
        })
        mainActivity.apiService.getActorCastByMovie(movieId).enqueue(object : Callback<ActorCast> {
            override fun onFailure(call: Call<ActorCast>, t: Throwable) {
                showToastCheckNetwork()
            }

            override fun onResponse(call: Call<ActorCast>, response: Response<ActorCast>) {
                val actorCast = response.body()
                val actors = actorCast?.cast?.map { it.name }
                actorCastContent?.let {
                    it.text = actors?.joinToString(", ")
                }
            }
        })
    }

    private fun markFavoriteMedia() {
        mainActivity.account?.id?.let {
            mainActivity.apiService.markFavoriteMovie(
                accountId = it,
                sessionId = mainActivity.sessionId,
                mediaType = "movie",
                mediaId = movieId,
                favorite = true
            ).enqueue(object : Callback<ResponseStatus> {
                override fun onFailure(call: Call<ResponseStatus>, t: Throwable) {
                    showToastCheckNetwork()
                }

                override fun onResponse(
                    call: Call<ResponseStatus>,
                    response: Response<ResponseStatus>
                ) {
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
            })
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