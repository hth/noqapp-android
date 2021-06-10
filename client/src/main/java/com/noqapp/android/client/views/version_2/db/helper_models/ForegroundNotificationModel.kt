package com.noqapp.android.client.views.version_2.db.helper_models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.noqapp.android.common.fcm.data.speech.JsonTextToSpeech
import com.noqapp.android.common.model.types.order.PurchaseOrderStateEnum

@Entity(tableName = "foreground_notification")
class ForegroundNotificationModel {
    @PrimaryKey(autoGenerate = true)
    var key: Long? = null
    @ColumnInfo(name = "go_to")
    var goTo = ""
    @ColumnInfo(name = "message_origin")
    var messageOrigin = ""
    @ColumnInfo(name = "current_serving")
    var currentServing = ""
    @ColumnInfo(name = "display_serving_number")
    var displayServingNumber = ""
    @ColumnInfo(name = "text_to_speeches")
    var jsonTextToSpeeches: List<JsonTextToSpeech?>? = null
    @ColumnInfo(name = "message_id")
    var msgId = ""
    @ColumnInfo(name = "purchase_order_state")
    var purchaseOrderStateEnum = PurchaseOrderStateEnum.IN
    @ColumnInfo(name = "qr_code")
    var qrCode = ""
    @ColumnInfo(name = "for_update_flag")
    var forUpdateFlag = false
}