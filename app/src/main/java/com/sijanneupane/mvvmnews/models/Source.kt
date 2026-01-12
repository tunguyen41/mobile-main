package com.sijanneupane.mvvmnews.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Source(
    @SerializedName("id")
    val id: String? = null,

    @SerializedName("name")
    val name: String? = null,

    // GNews hay có source.url
    @SerializedName("url")
    val url: String? = null
) : Serializable
