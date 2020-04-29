package com.example.kinoshop.model

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


data class BodyFavorite(
    @SerializedName("media_type")
    @Expose
    private val mediaType: String,
    @SerializedName("media_id")
    @Expose
    private val mediaId: Long,
    @SerializedName("favorite")
    @Expose
    private val favorite: Boolean
)