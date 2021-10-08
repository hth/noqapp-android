package com.noqapp.android.client.views.version_2.db.converters

import androidx.room.TypeConverter
import com.noqapp.android.common.model.types.category.RentalTypeEnum

class RentalTypeConverter {
    @TypeConverter
    fun toRentalType(value: String): RentalTypeEnum {
        return RentalTypeEnum.valueOf(value)
    }

    @TypeConverter
    fun toRentalTypeString(value: RentalTypeEnum): String {
        return value.name
    }
}