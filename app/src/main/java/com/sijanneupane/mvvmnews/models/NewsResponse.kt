package com.sijanneupane.mvvmnews.models

import com.google.gson.annotations.SerializedName

data class NewsResponse(
    @SerializedName("totalArticles") val totalArticles: Int? = null,
    @SerializedName("totalResults")  val totalResults: Int? = null,
    @SerializedName("articles")      val articles: MutableList<Article> = mutableListOf()
) {
    val totalCount: Int get() = totalArticles ?: totalResults ?: 0
}
