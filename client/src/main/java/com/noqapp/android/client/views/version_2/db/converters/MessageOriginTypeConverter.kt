package com.noqapp.android.client.views.version_2.db.converters

import androidx.room.TypeConverter
import com.noqapp.android.common.model.types.MessageOriginEnum

class MessageOriginTypeConverter {

    @TypeConverter
    fun getPurchaseOrderStateEnum(value: String): MessageOriginEnum {
        return MessageOriginEnum.valueOf(value)
    }

    @TypeConverter
    fun toPurchaseOrderStatusTypeString(value: MessageOriginEnum): String {
        return value.name
    }

}