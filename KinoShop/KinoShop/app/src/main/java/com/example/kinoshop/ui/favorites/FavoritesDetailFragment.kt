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
import com.example.kinoshop.model.ActorCast
import com.example.kinoshop.model.BodyFavorite
import com.example.kinoshop.model.MovieDetail
import com.example.kinoshop.model.ResponseStatus
import kotlinx.android.synthetic.main.fragment_favorite_detail.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FavoritesDetailFragment : Fragment() {
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
        mainActivity.apiService.getMovie(movieId).enqueue(object : Callback<MovieDetail> {
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
                val countries = movie?.productionCountries?.map { it.name }
                countriesContent.text = countries?.joinToString(", ")
            }
        })
        mainActivity.apiService.getActorCastByMovie(movieId).enqueue(object : Callback<ActorCast> {
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

    private fun markFavoriteMedia() {

        val bodyFavorite = BodyFavorite(
            "movie",
            movieId,
            false
        )

        mainActivity.apiService.deleteFavoriteMovie(
            accountId = mainActivity.account?.id!!,
            sessionId = mainActivity.sessionId,
            bodyFavorite = bodyFavorite
        ).enqueue(object : Callback<ResponseStatus> {
            override fun onFailure(call: Call<ResponseStatus>, t: Throwable) {
                showToastCheckNetwork()
            }

            override fun onResponse(
                call: Call<ResponseStatus>,
                response: Response<ResponseStatus>
            ) {
                if (response.body() != null) {
                    Toast.makeText(
                        context,
                        getString(R.string.movie_remove_favorite),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        context,
                        getString(R.string.check_auth_data),
                        Toast.LENGTH_SHORT
                    ).show()
                }
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