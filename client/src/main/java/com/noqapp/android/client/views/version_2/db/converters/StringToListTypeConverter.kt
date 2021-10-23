package com.noqapp.android.client.views.version_2.db.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class StringToListTypeConverter {
    @TypeConverter
    fun toImagePathList(value: String): List<String> {
        return Gson().fromJson(value, object : TypeToken<List<String?>?>() {}.type)
    }

    @TypeConverter
    fun toImagePathString(value: List<String>): String {
        return Gson().toJson(value)
    }
}