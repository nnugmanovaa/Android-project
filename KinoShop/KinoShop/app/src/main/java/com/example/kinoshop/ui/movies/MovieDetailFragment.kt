package com.example.kinoshop.ui.movies

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.kinoshop.R
import com.example.kinoshop.api.Api
import com.example.kinoshop.model.ActorCast
import com.example.kinoshop.model.MovieDetail
import kotlinx.android.synthetic.main.fragment_movie_detail.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MovieDetailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_movie_detail, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var movieId = 0L
        arguments?.let {
            val safeArgs = MovieDetailFragmentArgs.fromBundle(it)
            movieId = safeArgs.movieId
        }
        val api = Api()
        val apiService = api.serviceInitialize()

        apiService.getMovie(movieId).enqueue(object : Callback<MovieDetail> {
            override fun onFailure(call: Call<MovieDetail>, t: Throwable) {
                showToastCheckNetwork()
            }

            override fun onResponse(call: Call<MovieDetail>, response: Response<MovieDetail>) {
                val movie = response.body()
                collapsingToolbar.title = movie?.title
                context?.let {
                    Glide.with(it)
                        .load("${getString(R.string.download_image_url)}${movie?.posterPath}")
                        .placeholder(R.drawable.ic_movies)
                        .centerCrop()
                        .into(posterImage)
                }

                overviewContent.text = movie?.overview
                val genres = movie?.genres?.map { it.name }
                genreContent.text = genres?.joinToString(", ")
            }
        })
        apiService.getActorCastByMovie(movieId).enqueue(object : Callback<ActorCast> {
            override fun onFailure(call: Call<ActorCast>, t: Throwable) {
                showToastCheckNetwork()
            }

            override fun onResponse(call: Call<ActorCast>, response: Response<ActorCast>) {
                val actorCast = response.body()
                val actors = actorCast?.cast?.map { it.name }
                actorCastContent.text = actors?.joinToString(", ")
            }
        })
    }

    private fun showToastCheckNetwork() {
        Toast.makeText(
            context,
            getString(R.string.check_network_connection),
            Toast.LENGTH_SHORT
        ).show()
    }
}