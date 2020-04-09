package com.example.kinoshop.ui.movies

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
import kotlinx.android.synthetic.main.fragment_movies.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MoviesFragment : Fragment() {

    private lateinit var moviesAdapter: MoviesAdapter
    private lateinit var mainActivity: MainActivity
    private var page = 1
    private var totalPages = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_movies, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mainActivity = activity as MainActivity
        super.onViewCreated(view, savedInstanceState)
        initMoviesFeed()
        getMovies()
        swipeRefresh.setOnRefreshListener {
            loadNewMovies()
        }
    }

    private fun getMovies() {
        mainActivity.apiService.getMovies(page).enqueue(object : Callback<Movies> {
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
        moviesAdapter.setMoviesList(movies.results)
    }

    private fun insertMoviesList(movies: Movies) {
        moviesAdapter.insertNewMovies(movies.results)
    }

    private fun loadNewMovies() {
        if (page != totalPages) {
            moviesAdapter.needShowLoading()
            mainActivity.apiService.getMovies(page++).enqueue(object : Callback<Movies> {
                override fun onFailure(call: Call<Movies>, t: Throwable) {
                    showToastCheckNetwork()
                    swipeRefresh.isRefreshing = false
                }

                override fun onResponse(call: Call<Movies>, response: Response<Movies>) {
                    response.body()?.let {
                        insertMoviesList(it)
                    }
                    swipeRefresh.isRefreshing = false
                }
            })
        } else {
            swipeRefresh.isRefreshing = false
        }
    }

    private fun initMoviesFeed() {
        moviesAdapter = MoviesAdapter { loadNewMovies() }
        recyclerView.adapter = moviesAdapter
        addClickListener()
    }

    private fun addClickListener() {
        moviesAdapter.addOnClickMovie {
            val action =
                MoviesFragmentDirections.actionClickToMovie(moviesAdapter.getMovieIdByPosition(it.adapterPosition))
            view?.findNavController()?.navigate(action)
        }
    }
}
