package com.sijanneupane.mvvmnews.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sijanneupane.mvvmnews.NewsApplication
import com.sijanneupane.mvvmnews.db.User
import com.sijanneupane.mvvmnews.models.Article
import com.sijanneupane.mvvmnews.models.NewsResponse
import com.sijanneupane.mvvmnews.repository.NewsRepository
import com.sijanneupane.mvvmnews.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class NewsViewModel(
    app: Application,
    val newsRepository: NewsRepository
) : AndroidViewModel(app) {

    private val TAG = "NewsViewModel"

    // --- THÊM CODE MỚI ---
    val userLoginStatus = MutableLiveData<Resource<User>>()
    val userRegisterStatus = MutableLiveData<Resource<String>>()

    val breakingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    val searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()

    var breakingNewsPage = 1
    var searchNewsPage = 1

    var breakingNewsResponse: NewsResponse? = null
    var searchNewsResponse: NewsResponse? = null

    private var lastSearchQuery: String? = null

    init {
        getBreakingNews("in")
    }

    fun login(username: String, pass: String) = viewModelScope.launch {
        userLoginStatus.postValue(Resource.Loading())
        try {
            val user = newsRepository.loginUser(username, pass)
            if (user != null) {
                userLoginStatus.postValue(Resource.Success(user))
            } else {
                userLoginStatus.postValue(Resource.Error("Sai tài khoản hoặc mật khẩu"))
            }
        } catch (t: Throwable) {
            userLoginStatus.postValue(Resource.Error(t.message ?: "Lỗi hệ thống"))
        }
    }

    fun register(user: User) = viewModelScope.launch {
        userRegisterStatus.postValue(Resource.Loading())
        try {
            val exist = newsRepository.checkUserExist(user.username)
            if (exist == null) {
                newsRepository.registerUser(user)
                userRegisterStatus.postValue(Resource.Success("Đăng ký thành công!"))
            } else {
                userRegisterStatus.postValue(Resource.Error("Tài khoản đã tồn tại"))
            }
        } catch (t: Throwable) {
            Log.e(TAG, "Register error", t)
            userRegisterStatus.postValue(Resource.Error("Lỗi đăng ký: ${t.message}"))
        }
    }

    fun getBreakingNews(countryCode: String) = viewModelScope.launch {
        safeBreakingNewsCall(countryCode)
    }

    fun searchNews(searchQuery: String) = viewModelScope.launch {
        // Nếu query mới -> reset page + cache để khỏi trộn kết quả cũ
        val q = searchQuery.trim()
        if (lastSearchQuery != q) {
            lastSearchQuery = q
            searchNewsPage = 1
            searchNewsResponse = null
        }
        safeSearchNewsCall(q)
    }

    private fun handleBreakingNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                breakingNewsPage++
                if (breakingNewsResponse == null) {
                    breakingNewsResponse = resultResponse
                } else {
                    val oldArticles = breakingNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(breakingNewsResponse ?: resultResponse)
            }
        }
        // log thêm error body nếu có
        Log.e(TAG, "BreakingNews error: ${response.code()} ${response.message()}")
        return Resource.Error(response.message())
    }

    private fun handleSearchNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                searchNewsPage++
                if (searchNewsResponse == null) {
                    searchNewsResponse = resultResponse
                } else {
                    val oldArticles = searchNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(searchNewsResponse ?: resultResponse)
            }
        }
        Log.e(TAG, "SearchNews error: ${response.code()} ${response.message()}")
        return Resource.Error(response.message())
    }

    fun saveArticle(article: Article) = viewModelScope.launch {
        newsRepository.upsert(article)
    }

    fun getSavedArticle() = newsRepository.getSavedNews()

    fun deleteSavedArticle(article: Article) = viewModelScope.launch {
        newsRepository.deleteArticle(article)
    }

    private suspend fun safeBreakingNewsCall(countryCode: String) {
        breakingNews.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = newsRepository.getBreakingNews(countryCode, breakingNewsPage)
                breakingNews.postValue(handleBreakingNewsResponse(response))
            } else {
                breakingNews.postValue(Resource.Error("No Internet Connection"))
            }
        } catch (t: Throwable) {
            // ✅ IN RA LỖI THẬT
            Log.e(TAG, "BreakingNews exception", t)

            when (t) {
                is IOException -> breakingNews.postValue(Resource.Error("Network Failure"))
                else -> breakingNews.postValue(
                    Resource.Error(t.message ?: t.toString())
                )
            }
        }
    }

    private suspend fun safeSearchNewsCall(searchQuery: String) {
        searchNews.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = newsRepository.searchNews(searchQuery, searchNewsPage)
                searchNews.postValue(handleSearchNewsResponse(response))
            } else {
                searchNews.postValue(Resource.Error("No Internet Connection"))
            }
        } catch (t: Throwable) {
            Log.e(TAG, "SearchNews exception", t)

            when (t) {
                is IOException -> searchNews.postValue(Resource.Error("Network Failure"))
                else -> searchNews.postValue(
                    Resource.Error(t.message ?: t.toString())
                )
            }
        }
    }

    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<NewsApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }
}
