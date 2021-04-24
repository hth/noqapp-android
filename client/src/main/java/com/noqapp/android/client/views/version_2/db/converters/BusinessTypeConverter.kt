package com.noqapp.android.client.views.version_2.db.converters

import androidx.room.TypeConverter
import com.noqapp.android.common.model.types.BusinessTypeEnum


class BusinessTypeConverter {
    @TypeConverter
    fun getBusinessType(value: String): BusinessTypeEnum {
        return BusinessTypeEnum.valueOf(value)
    }

    @TypeConverter
    fun toBusinessTypeString(value: BusinessTypeEnum): String {
        return value.name
    }
}