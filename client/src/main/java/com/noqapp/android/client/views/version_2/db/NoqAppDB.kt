package com.noqapp.android.client.views.version_2.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.noqapp.android.common.pojos.DisplayNotification

@Database(entities = [DisplayNotification::class], version = 1)
abstract class NoqAppDB : RoomDatabase() {

    abstract fun notificationDao(): NotificationDao
    abstract fun reviewDao(): ReviewDao

    companion object {
        private var instance: NoqAppDB? = null

        @Synchronized
        fun getNoqAppDbInstance(context: Context): NoqAppDB {
            if (instance == null) {
                synchronized(NoqAppDB::class) {
                    instance = Room.databaseBuilder(context, NoqAppDB::class.java, "noq_app_database").build()
                }
            }
            return instance!!
        }
    }
}