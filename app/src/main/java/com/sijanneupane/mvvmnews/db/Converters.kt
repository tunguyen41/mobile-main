package com.sijanneupane.mvvmnews.db

import androidx.room.TypeConverter
import com.sijanneupane.mvvmnews.models.Source

class Converters {

    @TypeConverter
    fun fromSource(source: Source?): String? = source?.name

    @TypeConverter
    fun toSource(name: String?): Source? =
        if (name == null) null else Source(name = name)
}
