package com.sijanneupane.mvvmnews.repository

import com.sijanneupane.mvvmnews.api.RetrofitInstance
import com.sijanneupane.mvvmnews.db.ArticleDatabase
import com.sijanneupane.mvvmnews.db.User
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
        db.getArticleDao().upsert(article)

    fun getSavedNews() =
        db.getArticleDao().getAllArticles()

    suspend fun deleteArticle(article: Article) =
        db.getArticleDao().deleteArticle(article)

    // Xử lý Đăng ký
    suspend fun registerUser(user: User) = db.getUserDao().upsert(user)

    // Kiểm tra user đã tồn tại chưa
    suspend fun checkUserExist(username: String) = db.getUserDao().checkUserExist(username)

    // Xử lý Đăng nhập
    suspend fun loginUser(username: String, pass: String) = db.getUserDao().getUser(username, pass)
}
