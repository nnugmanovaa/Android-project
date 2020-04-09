package com.example.kinoshop.ui.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.kinoshop.MainActivity
import com.example.kinoshop.R
import com.example.kinoshop.model.Movies
import com.example.kinoshop.ui.movies.MoviesAdapter
import kotlinx.android.synthetic.main.fragment_movies.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FavoritesFragment : Fragment() {

    private lateinit var favoritesAdapter: MoviesAdapter
    private lateinit var mainActivity: MainActivity
    private var page = 1
    private var totalPages = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_favorites, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity = activity as MainActivity
        if (mainActivity.isSignIn) {
            initMoviesFeed()
            getFavorite()
            swipeRefresh.setOnRefreshListener {
                loadNewFavoriteMovies()
            }
        } else {
            Toast.makeText(context, getString(R.string.req_auth), Toast.LENGTH_SHORT).show()
        }
    }

    private fun getFavorite() {
        mainActivity.apiService.getFavoriteMovieByAccountId(
            mainActivity.account?.id!!, mainActivity.sessionId,
            page
        ).enqueue(object : Callback<Movies> {
            override fun onFailure(call: Call<Movies>, t: Throwable) {
                showToastCheckNetwork()
            }

            override fun onResponse(call: Call<Movies>, response: Response<Movies>) {
                response.body()?.let {
                    totalPages = it.totalPages
                    setMoviesList(it)
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

    private fun setMoviesList(movies: Movies) {
        favoritesAdapter.setMoviesList(movies.results)
    }

    private fun insertMoviesList(movies: Movies) {
        favoritesAdapter.insertNewMovies(movies.results)
    }

    private fun loadNewFavoriteMovies() {
        if (page != totalPages) {
            favoritesAdapter.needShowLoading()
            mainActivity.apiService.getFavoriteMovieByAccountId(
                mainActivity.account?.id!!, mainActivity.sessionId,
                page++
            ).enqueue(object : Callback<Movies> {
                override fun onFailure(call: Call<Movies>, t: Throwable) {
                    showToastCheckNetwork()
                    swipeRefresh.isRefreshing = false
                }

                override fun onResponse(call: Call<Movies>, response: Response<Movies>) {
                    response.body()?.let {
                        insertMoviesList(it)
                    }
                    swipeRefresh.isRefreshing = false
                    page++
                }
            })
        } else {
            swipeRefresh.isRefreshing = false
        }
    }

    private fun initMoviesFeed() {
        favoritesAdapter = MoviesAdapter { loadNewFavoriteMovies() }
        recyclerView.adapter = favoritesAdapter
        addClickListener()
    }

    private fun addClickListener() {
        favoritesAdapter.addOnClickMovie {
            val action =
                FavoritesFragmentDirections.actionClickToMovie(
                    favoritesAdapter.getMovieIdByPosition(
                        it.adapterPosition
                    )
                )
            view?.findNavController()?.navigate(action)
        }
    }
}
