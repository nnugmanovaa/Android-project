package com.example.kinoshop.ui.movies

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.kinoshop.R
import com.example.kinoshop.api.Api
import com.example.kinoshop.api.ApiService
import com.example.kinoshop.model.MovieDetail
import com.example.kinoshop.model.MovieDetailDao
import com.example.kinoshop.model.MovieDetailDatabase
import com.example.kinoshop.model.Movies
import kotlinx.android.synthetic.main.fragment_movies.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.*
import java.lang.Exception

class MoviesFragment : Fragment(), CoroutineScope {

    private lateinit var moviesAdapter: MoviesAdapter
    private lateinit var apiService: ApiService
    private var page = 1
    private var totalPages = 0

    private val job = Job()

    private var movieDetailDao: MovieDetailDao? = null

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_movies, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val api = Api()
        apiService = api.serviceInitialize()
        initMoviesFeed()

        movieDetailDao = MovieDetailDatabase.getDatabase(context = requireActivity()).movieDetailDao()
        getMoviesList()
        swipeRefresh.setOnRefreshListener {
            getMoviesList()
            loadNewMovies()

        }




    }


    fun getMoviesList(){
        launch{
            val list = withContext(Dispatchers.IO){

                try{
                    val response = apiService.getMoviesCoroutine(page)
                    if(response.isSuccessful){
                        val result = response.body()?.getResults()
                        Log.d("asd", movieDetailDao?.getAll().toString())
                        if (!result.isNullOrEmpty()){
                            movieDetailDao?.deleteAll()
                            movieDetailDao?.insertAll(result)
                        }
                        result
                    }
                    else{
                        movieDetailDao?.getAll()?: emptyList()
                    }
                } catch (e: Exception){
                    movieDetailDao?.getAll()?: emptyList()
                }
            }
            moviesAdapter.setMoviesList(list)
            moviesAdapter.notifyDataSetChanged()
//            val response = apiService.getMoviesCoroutine(page)
//            if (response.isSuccessful){
//                response.body()?.let {
//                    totalPages = it.totalPages
//                    setMoviesList(it)
//                }
//            }
        }
    }



    private fun showToastCheckNetwork() {
        Toast.makeText(
            context,
            getString(R.string.check_network_connection),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun setMoviesList(movies: Movies) {
        moviesAdapter.setMoviesList(movies.getResults())
    }

    private fun insertMoviesList(movies: Movies) {
        moviesAdapter.insertNewMovies(movies.getResults())
    }

    private fun loadNewMovies() {
        if (page != totalPages)
            launch{
                try {
                    val response = apiService.getMoviesCoroutine(page)
                    if (response.isSuccessful){
                        response.body()?.let {
                            insertMoviesList(it)
                        }
                        swipeRefresh.isRefreshing = false
                        page++
                    }
                }catch (e: Exception){

                }

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