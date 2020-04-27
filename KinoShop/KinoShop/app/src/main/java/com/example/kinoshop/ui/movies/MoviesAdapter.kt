package com.example.kinoshop.ui.movies

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kinoshop.R
import com.example.kinoshop.model.MovieDetail
import kotlinx.android.synthetic.main.item_list_movies.view.*

class MoviesAdapter(private val onLoadNewMovies: () -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var listItems = mutableListOf<MovieDetail>()
    private var needLoad = false
    private lateinit var onClickMovie: (MovieHolder) -> Unit

    private companion object {
        const val MOVIES = 1
        const val LOAD = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            LOAD -> LoadingHolder.createItemInstance(parent)
            MOVIES -> MovieHolder.createItemInstance(parent)
            else -> LoadingHolder.createItemInstance(parent)
        }
    }

    override fun getItemCount(): Int = listItems.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position == listItems.lastIndex) {
            onLoadNewMovies()
        }

        when (holder) {
            is MovieHolder -> {
                holder.bindView(listItems[position])
                initListeners(holder)
            }
        }
    }

    fun setMoviesList(moviesList: List<MovieDetail>?) {
        listItems = moviesList as MutableList<MovieDetail>
        needLoad = false
        notifyDataSetChanged()
    }

    fun insertNewMovies(moviesList: List<MovieDetail>) {
        listItems.addAll(moviesList)
        needLoad = false
        notifyItemRangeInserted(listItems.lastIndex, moviesList.size)
    }

    fun addOnClickMovie(consumer: (MovieHolder) -> Unit) {
        onClickMovie = consumer
    }

    fun getMovieIdByPosition(position: Int) = listItems[position].id

    override fun getItemViewType(position: Int): Int {
        return if (position == listItems.size - 1 && needLoad) {
            LOAD
        } else {
            MOVIES
        }
    }

    fun needShowLoading() {
        needLoad = true
    }

    private fun initListeners(holder: RecyclerView.ViewHolder) {
        when (holder) {
            is MovieHolder -> {
                if (::onClickMovie.isInitialized) {
                    holder.addOnClickListener(onClickMovie)
                }
            }
        }
    }
}


class MovieHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    companion object {
        fun createItemInstance(parent: ViewGroup) = MovieHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_list_movies,
                parent, false
            )
        )
    }

    fun bindView(movie: MovieDetail) {
        val context = itemView.context
        val imageUrl = movie.posterPath
        Glide.with(context)
            .load("${context.getString(R.string.download_image_url)}$imageUrl")
            .placeholder(R.drawable.ic_movies)
            .into(itemView.imageView)
        itemView.title.text = movie.title
        itemView.releaseDate.text = movie.releaseDate
    }

    fun addOnClickListener(consumer: (MovieHolder) -> Unit) {
        itemView.setOnClickListener { consumer(this) }
    }
}

class LoadingHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    companion object {
        fun createItemInstance(parent: ViewGroup) = LoadingHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_progress_page,
                parent, false
            )
        )
    }

}