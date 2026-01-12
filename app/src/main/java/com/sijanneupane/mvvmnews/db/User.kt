package com.sijanneupane.mvvmnews.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "users"
)
data class User(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    val username: String, // Tên đăng nhập hoặc Email
    val password: String  // Mật khẩu
)