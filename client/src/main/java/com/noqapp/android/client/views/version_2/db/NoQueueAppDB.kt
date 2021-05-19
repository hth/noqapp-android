package com.noqapp.android.client.views.version_2.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.noqapp.android.client.presenter.beans.JsonTokenAndQueue
import com.noqapp.android.client.presenter.beans.ReviewData
import com.noqapp.android.client.views.version_2.db.converters.BusinessTypeConverter
import com.noqapp.android.client.views.version_2.db.converters.JsonPurchaseOrderTypeConverter
import com.noqapp.android.client.views.version_2.db.converters.PurchaseOrderStateTypeConverter
import com.noqapp.android.client.views.version_2.db.converters.QueueStatusTypeConverter
import com.noqapp.android.common.pojos.DisplayNotification

@Database(entities = [DisplayNotification::class, ReviewData::class, JsonTokenAndQueue::class], version = 1)
@TypeConverters(BusinessTypeConverter::class, QueueStatusTypeConverter::class, PurchaseOrderStateTypeConverter::class, JsonPurchaseOrderTypeConverter::class)
abstract class NoQueueAppDB : RoomDatabase() {

    abstract fun notificationDao(): NotificationDao
    abstract fun reviewDao(): ReviewDao
    abstract fun tokenAndQueueDao(): TokenAndQueueDao

    companion object {
        private var instance: NoQueueAppDB? = null

        @Synchronized
        fun dbInstance(context: Context): NoQueueAppDB {
            if (instance == null) {
                synchronized(NoQueueAppDB::class) {
                    instance = Room.databaseBuilder(context, NoQueueAppDB::class.java, "noq_app_database").build()
                }
            }
            return instance!!
        }
    }
}