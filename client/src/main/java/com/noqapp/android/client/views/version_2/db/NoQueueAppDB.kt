package com.noqapp.android.client.views.version_2.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.noqapp.android.client.presenter.beans.JsonTokenAndQueue
import com.noqapp.android.client.presenter.beans.ReviewData
import com.noqapp.android.client.views.version_2.db.converters.*
import com.noqapp.android.client.views.version_2.db.helper_models.ForegroundNotificationModel
import com.noqapp.android.common.pojos.DisplayNotification
import com.noqapp.android.common.pojos.PropertyRentalEntity

@Database(
    entities = [DisplayNotification::class, ReviewData::class, JsonTokenAndQueue::class, ForegroundNotificationModel::class, PropertyRentalEntity::class],
    version = 2
)
@TypeConverters(
    BusinessTypeConverter::class,
    QueueStatusTypeConverter::class,
    PurchaseOrderStateTypeConverter::class,
    JsonPurchaseOrderTypeConverter::class,
    TextToSpeechTypeConverter::class,
    RentalTypeConverter::class,
    CoordinateConverter::class
)
abstract class NoQueueAppDB : RoomDatabase() {

    abstract fun notificationDao(): NotificationDao
    abstract fun reviewDao(): ReviewDao
    abstract fun tokenAndQueueDao(): TokenAndQueueDao
    abstract fun foregroundNotificationDao(): ForegroundNotificationDao
    abstract fun propertyRentalDao(): PropertyRentalDao

    companion object {
        private var instance: NoQueueAppDB? = null

        @Synchronized
        fun dbInstance(context: Context): NoQueueAppDB {
            if (instance == null) {
                synchronized(NoQueueAppDB::class) {
                    instance =
                        Room.databaseBuilder(context, NoQueueAppDB::class.java, "noqueue_database")
                            .fallbackToDestructiveMigration()
                            .build()
                }
            }
            return instance!!
        }
    }
}
