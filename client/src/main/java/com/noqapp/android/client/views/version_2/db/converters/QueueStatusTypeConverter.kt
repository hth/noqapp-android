package com.noqapp.android.client.views.version_2.db.converters

import androidx.room.TypeConverter
import com.noqapp.android.common.model.types.QueueStatusEnum

class QueueStatusTypeConverter {

    @TypeConverter
    fun getQueueStatusType(value: String): QueueStatusEnum {
        return QueueStatusEnum.valueOf(value)
    }

    @TypeConverter
    fun toQueueStatusTypeString(value: QueueStatusEnum): String {
        return value.name
    }

}