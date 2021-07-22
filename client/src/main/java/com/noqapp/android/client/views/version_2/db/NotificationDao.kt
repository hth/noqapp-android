package com.noqapp.android.client.views.version_2.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.noqapp.android.common.pojos.DisplayNotification

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notification ORDER BY created_date DESC")
    fun getNotificationsList(): LiveData<List<DisplayNotification>>

    @Query("SELECT COUNT(*) FROM notification WHERE status = :status")
    fun getNotificationCount(status: String): LiveData<Int>

    @Query("DELETE FROM notification")
    suspend fun deleteNotifications()

    @Query("DELETE FROM notification WHERE `key`=:key")
    suspend fun deleteNotification(key: String?)

    @Insert
    suspend fun insertNotification(displayNotification: DisplayNotification)

    @Update
    suspend fun updateNotification(displayNotification: DisplayNotification)
}
