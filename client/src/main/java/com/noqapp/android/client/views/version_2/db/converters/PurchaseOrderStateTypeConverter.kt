package com.noqapp.android.client.views.version_2.db.converters

import androidx.room.TypeConverter
import com.noqapp.android.common.model.types.order.PurchaseOrderStateEnum

class PurchaseOrderStateTypeConverter {

    @TypeConverter
    fun getPurchaseOrderStateEnum(value: String): PurchaseOrderStateEnum {
        return PurchaseOrderStateEnum.valueOf(value)
    }

    @TypeConverter
    fun toPurchaseOrderStatusTypeString(value: PurchaseOrderStateEnum): String {
        return value.name
    }

}