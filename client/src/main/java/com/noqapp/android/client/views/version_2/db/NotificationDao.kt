package com.noqapp.android.client.views.version_2.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.noqapp.android.common.pojos.DisplayNotification

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notification")
    fun getNotificationsList(): LiveData<List<DisplayNotification>>

    @Query("SELECT COUNT(*) FROM notification WHERE status = :status")
    fun getNotificationCount(status: String): LiveData<Int>

    @Query("DELETE FROM notification")
    fun deleteNotifications()

    @Insert
    fun insertNotification(displayNotification: DisplayNotification)

    @Update
    fun updateNotification(displayNotification: DisplayNotification)
}