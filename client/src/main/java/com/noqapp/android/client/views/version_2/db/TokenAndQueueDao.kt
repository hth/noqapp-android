package com.noqapp.android.client.views.version_2.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.noqapp.android.client.presenter.beans.JsonTokenAndQueue

@Dao
interface TokenAndQueueDao {

    @Query("SELECT * FROM token_queue WHERE historyQueue!=1 ORDER BY create_date ASC ")
    fun getCurrentQueueList(): LiveData<List<JsonTokenAndQueue>>

    @Query("SELECT * FROM token_queue WHERE historyQueue!=1 AND qr_code=:qrCode AND token=:token")
    fun getCurrentQueueObject(qrCode: String, token: Int): LiveData<JsonTokenAndQueue>

    @Query("SELECT * FROM token_queue WHERE historyQueue!=1 AND qr_code=:qrCode")
    fun findByQRCode(qrCode: String): LiveData<JsonTokenAndQueue>

    @Query("SELECT * FROM token_queue WHERE historyQueue!=1 AND qr_code=:qrCode")
    fun getCurrentQueueObjectList(qrCode: String?): LiveData<List<JsonTokenAndQueue>>

    @Query("SELECT * FROM token_queue WHERE historyQueue=1 AND qr_code=:qrCode AND token=:token")
    fun getHistoryQueueObject(qrCode: String, token: Int): LiveData<JsonTokenAndQueue>

    @Query("SELECT * , MAX(create_date) FROM token_queue WHERE historyQueue=1 GROUP BY qr_code")
    fun getHistoryQueueList(): LiveData<List<JsonTokenAndQueue>>

    @Insert
    fun saveCurrentQueue(list: List<JsonTokenAndQueue>)

    @Insert
    fun saveJoinQueueObject(list: JsonTokenAndQueue)

    @Query("UPDATE token_queue SET serving_number=:servingNumber, display_serving_number=:displayServingNumber WHERE qr_code=:qrCode AND token=:token")
    fun updateCurrentListQueueObject(qrCode: String?, servingNumber: String, displayServingNumber: String, token: Int)

    @Query("UPDATE token_queue SET puchase_order_state=:orderState WHERE qr_code=:qrCode AND token=:token")
    fun updateCurrentListOrderObject(qrCode: String?, orderState: String, token: Int)

    @Query("DELETE FROM token_queue WHERE historyQueue!=1")
    fun deleteCurrentQueue()

    @Query("DELETE FROM token_queue WHERE historyQueue=1")
    fun deleteHistoryQueue()

    @Query("DELETE FROM token_queue WHERE historyQueue!=1 AND qr_code=:qrCode AND token=:token")
    fun deleteTokenQueue(qrCode: String, token: Int)

}