package com.sijanneupane.mvvmnews.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {

    // Đăng ký: Thêm user mới
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(user: User): Long

    // Đăng nhập: Tìm user khớp username và password
    @Query("SELECT * FROM users WHERE username = :username AND password = :password")
    suspend fun getUser(username: String, password: String): User?

    // Kiểm tra tồn tại: Để tránh đăng ký trùng tên
    @Query("SELECT * FROM users WHERE username = :username")
    suspend fun checkUserExist(username: String): User?
}