package com.noqapp.android.merchant.model.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import static com.noqapp.android.merchant.model.database.DatabaseTable.Notification;
import static com.noqapp.android.merchant.model.database.DatabaseTable.PreferredStore;

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
                + Notification.SEQUENCE + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Notification.CREATE_DATE + " TEXT "+
                //+ "PRIMARY KEY(`" + Notification.KEY + "`)" +

                ");");
    }

    static void createTablePreferredStore(SQLiteDatabase db) {
        Log.d(TAG, "executing createTableNotification");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + PreferredStore.TABLE_NAME + "("
                + PreferredStore.PRODUCT_ID + " TEXT, "
                + PreferredStore.BIZ_STORE_ID + " TEXT, "
                + PreferredStore.DISPLAY_PRICE + " TEXT, "
                + PreferredStore.PRODUCT_NAME + " TEXT, "
                + PreferredStore.PRODUCT_INFO + " TEXT, "
                + PreferredStore.STORE_CAT_ID + " TEXT, "
                + PreferredStore.PRODUCT_TYPE + " TEXT, "
                + PreferredStore.PRODUCT_UNIT_VALUE + " TEXT, "
                + PreferredStore.PRODUCT_UNIT_MESAURE + " TEXT "+
                ");");
    }

    static void createAllTable(SQLiteDatabase db) {
        createTableNotification(db);
        createTablePreferredStore(db);
    }
}
