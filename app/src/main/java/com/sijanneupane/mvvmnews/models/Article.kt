package com.sijanneupane.mvvmnews.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(tableName = "articles")
data class Article(
    // ✅ Đổi tên id của DB để Gson không map JSON "id" vào đây nữa
    @PrimaryKey(autoGenerate = true)
    var dbId: Int? = null,

    // ✅ Nếu API có "id" dạng chuỗi thì map vào đây (không bắt buộc dùng)
    @SerializedName("id")
    val apiId: String? = null,

    @SerializedName("title")
    val title: String?,

    @SerializedName("description")
    val description: String?,

    @SerializedName("content")
    val content: String?,

    @SerializedName("publishedAt")
    val publishedAt: String?,

    @SerializedName("url")
    val url: String?,

    // GNews
    @SerializedName("image")
    val image: String?,

    // NewsAPI
    @SerializedName("urlToImage")
    val urlToImage: String?,

    @SerializedName("source")
    val source: Source?
) : Serializable
