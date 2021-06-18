package com.noqapp.android.client.views.version_2.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.noqapp.android.client.views.version_2.db.helper_models.ForegroundNotificationModel

@Dao
interface ForegroundNotificationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertForeGroundNotification(foregroundNotificationModel: ForegroundNotificationModel)

    @Query("SELECT * FROM foreground_notification")
    fun getForegroundNotification(): LiveData<ForegroundNotificationModel>

    @Query("DELETE FROM foreground_notification")
    suspend fun deleteForegroundNotification()
}