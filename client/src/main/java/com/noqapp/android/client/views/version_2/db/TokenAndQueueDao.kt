package com.noqapp.android.client.views.version_2.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.noqapp.android.client.presenter.beans.JsonTokenAndQueue
import com.noqapp.android.common.model.types.order.PurchaseOrderStateEnum

@Dao
interface TokenAndQueueDao {

    @Query("SELECT * FROM token_queue WHERE history_queue!=1 ORDER BY create_date ASC ")
    fun getCurrentQueueList(): LiveData<List<JsonTokenAndQueue>>

    @Query("SELECT * FROM token_queue WHERE history_queue!=1 AND qr_code=:qrCode AND token=:token")
    suspend fun getCurrentQueueObject(qrCode: String?, token: Int?): JsonTokenAndQueue?

    @Query("SELECT * FROM token_queue WHERE history_queue!=1 AND qr_code=:qrCode")
    fun findByQRCode(qrCode: String): LiveData<JsonTokenAndQueue>

    @Query("SELECT * FROM token_queue WHERE history_queue!=1 AND qr_code=:qrCode")
    suspend fun getCurrentQueueObjectList(qrCode: String?): List<JsonTokenAndQueue>?

    @Query("SELECT * FROM token_queue WHERE history_queue=1 AND qr_code=:qrCode AND token=:token")
    suspend fun getHistoryQueueObject(qrCode: String?, token: Int?): JsonTokenAndQueue?

    @Query("SELECT * , MAX(create_date) FROM token_queue WHERE history_queue=1 GROUP BY qr_code")
    fun getHistoryQueueList(): LiveData<List<JsonTokenAndQueue>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveCurrentQueue(list: List<JsonTokenAndQueue>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveJoinQueueObject(list: JsonTokenAndQueue)

    @Query("UPDATE token_queue SET serving_number=:servingNumber, display_serving_number=:displayServingNumber WHERE qr_code=:qrCode AND token=:token")
    suspend fun updateCurrentListQueueObject(qrCode: String?, servingNumber: String, displayServingNumber: String, token: Int)

    @Query("UPDATE token_queue SET purchase_order_state=:orderState WHERE qr_code=:qrCode AND token=:token")
    suspend fun updateCurrentListOrderObject(qrCode: String?, orderState: PurchaseOrderStateEnum, token: Int)

    @Query("UPDATE token_queue SET serving_number=:servingNumber, display_serving_number=:displayServingNumber, has_updated=1 WHERE qr_code=:qrCode AND token=:token")
    suspend fun updateCurrentListQueueObjectWithState(qrCode: String?, servingNumber: String, displayServingNumber: String, token: Int)

    @Query("UPDATE token_queue SET purchase_order_state=:orderState, has_updated=1 WHERE qr_code=:qrCode AND token=:token")
    suspend fun updateCurrentListOrderObjectWithState(qrCode: String?, orderState: PurchaseOrderStateEnum, token: Int)

    @Query("DELETE FROM token_queue WHERE history_queue!=1")
    suspend fun deleteCurrentQueue()

    @Query("DELETE FROM token_queue WHERE history_queue=1")
    suspend fun deleteHistoryQueue()

    @Query("DELETE FROM token_queue WHERE history_queue!=1 AND qr_code=:qrCode AND token=:token")
    suspend fun deleteTokenQueue(qrCode: String?, token: Int?)

}