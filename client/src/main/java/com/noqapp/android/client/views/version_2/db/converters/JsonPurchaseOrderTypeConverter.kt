package com.noqapp.android.client.views.version_2.db.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.noqapp.android.common.beans.store.JsonPurchaseOrder
import com.noqapp.android.common.model.types.order.PurchaseOrderStateEnum

class JsonPurchaseOrderTypeConverter {

    @TypeConverter
    fun getQueueStatusType(value: String?): JsonPurchaseOrder? {
        val purchaseOrder = Gson().fromJson(value, JsonPurchaseOrder::class.java)
        return purchaseOrder
    }

    @TypeConverter
    fun toQueueStatusTypeString(value: JsonPurchaseOrder?): String? {
        val json = Gson().toJson(value)
        return json
    }

}