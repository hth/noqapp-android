package com.noqapp.android.merchant.model.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import static com.noqapp.android.merchant.model.database.DatabaseTable.Notification;


/**
 * User: hitender
 * Date: 5/9/17 7:18 PM
 */

public class CreateTable {
    private static final String TAG = CreateTable.class.getSimpleName();

    private CreateTable() {
    }




    static void createTableNotification(SQLiteDatabase db) {
        Log.d(TAG, "executing createTableNotification");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + Notification.TABLE_NAME + "("
                + Notification.KEY + " TEXT, "
                + Notification.CODE_QR + " TEXT, "
                + Notification.BODY + " TEXT, "
                + Notification.TITLE + " TEXT, "
                + Notification.STATUS + " TEXT, "
                + Notification.SEQUENCE + " INTEGER PRIMARY KEY   AUTOINCREMENT, "
                + Notification.CREATE + " TEXT "+
                //+ "PRIMARY KEY(`" + Notification.KEY + "`)" +

                ");");
    }

    static void createAllTable(SQLiteDatabase db) {
        createTableNotification(db);
    }
}
