package com.noqapp.android.client.views.version_2.db.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.noqapp.android.common.fcm.data.speech.JsonTextToSpeech

class TextToSpeechTypeConverter {
    @TypeConverter
    fun getQueueStatusType(jsonString: String): List<JsonTextToSpeech?>? {
        return Gson().fromJson(jsonString, object : TypeToken<List<JsonTextToSpeech?>?>() {}.type)
    }

    @TypeConverter
    fun toQueueStatusTypeString(value: List<JsonTextToSpeech?>?): String {
        return Gson().toJson(value)
    }
}