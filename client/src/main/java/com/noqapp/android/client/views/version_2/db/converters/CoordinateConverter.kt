package com.noqapp.android.client.views.version_2.db.converters

import androidx.room.TypeConverter
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import java.lang.reflect.Type

class CoordinateConverter {
    @TypeConverter
    fun fromCoordinateValuesList(coordinateValues: List<Double>?): String? {
        val gson = Gson()
        val type: Type = object : TypeToken<List<Double>>() {}.type
        return gson.toJson(coordinateValues, type)
    }

    @TypeConverter
    fun toCoordinateValuesList(coordinateValuesString: String?): List<Double>? {
        val gson = Gson()
        val type: Type = object : TypeToken<List<Double>>() {}.type
        return gson.fromJson(coordinateValuesString, type)
    }
}