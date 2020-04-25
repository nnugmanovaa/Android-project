package com.example.kinoshop.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MovieDetailDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(list: List<MovieDetail>)

    @Query("SELECT * FROM movie_table")
    fun getAll(): List<MovieDetail>

    @Query("DELETE FROM movie_table")
    fun deleteAll()

    @Query( "SELECT * FROM movie_table WHERE id = :id")
    fun getMovieById(id: Long):  MovieDetail
}