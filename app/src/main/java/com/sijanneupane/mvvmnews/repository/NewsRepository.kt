package com.sijanneupane.mvvmnews.repository

import com.sijanneupane.mvvmnews.api.RetrofitInstance
import com.sijanneupane.mvvmnews.db.ArticleDatabase
import com.sijanneupane.mvvmnews.models.Article

class NewsRepository(
    private val db: ArticleDatabase
) {
    suspend fun getBreakingNews(countryCode: String, pageNumber: Int) =
        RetrofitInstance.api.getBreakingNews(
            countryCode = countryCode,
            pageNumber = pageNumber
        )

    suspend fun searchNews(searchQuery: String, pageNumber: Int) =
        RetrofitInstance.api.searchForNews(
            searchQuery = searchQuery,
            pageNumber = pageNumber
        )

    suspend fun upsert(article: Article) =
        db.articleDao().upsert(article)

    fun getSavedNews() =
        db.articleDao().getAllArticles()

    suspend fun deleteArticle(article: Article) =
        db.articleDao().deleteArticle(article)
}
