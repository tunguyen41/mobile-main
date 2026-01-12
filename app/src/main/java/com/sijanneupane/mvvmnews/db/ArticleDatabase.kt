package com.sijanneupane.mvvmnews.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sijanneupane.mvvmnews.models.Article

@Database(
    entities = [Article::class, User::class], // ✅ Đã có User
    version = 3,                              // ✅ Đã tăng version
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class ArticleDatabase : RoomDatabase() {

    abstract fun getArticleDao(): ArticleDao // Đổi tên cho thống nhất (tùy chọn)
    abstract fun getUserDao(): UserDao       // ✅ Đã thêm UserDao

    companion object {
        @Volatile
        private var INSTANCE: ArticleDatabase? = null
        private val LOCK = Any()

        // Cách viết chuẩn dùng operator invoke để gọi instance dễ hơn
        operator fun invoke(context: Context) = INSTANCE ?: synchronized(LOCK) {
            INSTANCE ?: createDatabase(context).also { INSTANCE = it }
        }

        private fun createDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                ArticleDatabase::class.java,
                "article_db.db"
            )
                .fallbackToDestructiveMigration() // ✅ Quan trọng: Xóa data cũ khi update version
                .build()
    }
}