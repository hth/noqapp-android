package com.noqapp.android.client.model.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import static com.noqapp.android.client.model.database.DatabaseTable.Review;
import static com.noqapp.android.client.model.database.DatabaseTable.TokenQueue;
import static com.noqapp.android.client.model.database.DatabaseTable.TokenQueueHistory;

/**
 * User: hitender
 * Date: 5/9/17 7:18 PM
 */

class CreateTable {
    private static final String TAG = CreateTable.class.getSimpleName();

    private CreateTable() {
    }

    private static void createTableTokenQueue(SQLiteDatabase db) {
        Log.d(TAG, "executing createTableTokenQueue");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TokenQueue.TABLE_NAME + "("
                + TokenQueue.CODE_QR + " TEXT , "
                + TokenQueue.BUSINESS_NAME + " TEXT ,"
                + TokenQueue.DISPLAY_NAME + " TEXT , "
                + TokenQueue.STORE_ADDRESS + " TEXT ,"
                + TokenQueue.COUNTRY_SHORT_NAME + " TEXT , "
                + TokenQueue.STORE_PHONE + " TEXT , "
                + TokenQueue.TOKEN_AVAILABLE_FROM + " TEXT , "
                + TokenQueue.START_HOUR + " TEXT , "
                + TokenQueue.END_HOUR + " TEXT , "
                + TokenQueue.TOPIC + " TEXT , "
                + TokenQueue.SERVING_NUMBER + " TEXT , "
                + TokenQueue.LAST_NUMBER + " TEXT , "
                + TokenQueue.TOKEN + " TEXT , "
                + TokenQueue.QUEUE_STATUS + " TEXT , "
                + TokenQueue.SERVICED_TIME + " TEXT , "
                + TokenQueue.CREATE_DATE + " TEXT , "
                + "PRIMARY KEY(`codeqr`,`createdate`)" +

                ");");
    }

    private static void createTableTokenQueueHistory(SQLiteDatabase db) {
        Log.d(TAG, "executing createTableTokenQueue");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TokenQueueHistory.TABLE_NAME + "("
                + TokenQueue.CODE_QR + " TEXT , "
                + TokenQueue.BUSINESS_NAME + " TEXT , "
                + TokenQueue.DISPLAY_NAME + " TEXT , "
                + TokenQueue.STORE_ADDRESS + " TEXT , "
                + TokenQueue.COUNTRY_SHORT_NAME + " TEXT , "
                + TokenQueue.STORE_PHONE + " TEXT , "
                + TokenQueue.TOKEN_AVAILABLE_FROM + " TEXT , "
                + TokenQueue.START_HOUR + " TEXT , "
                + TokenQueue.END_HOUR + " TEXT , "
                + TokenQueue.TOPIC + " TEXT , "
                + TokenQueue.SERVING_NUMBER + " TEXT , "
                + TokenQueue.LAST_NUMBER + " TEXT , "
                + TokenQueue.TOKEN + " TEXT , "
                + TokenQueue.QUEUE_STATUS + " TEXT , "
                + TokenQueue.SERVICED_TIME + " TEXT , "
                + TokenQueue.CREATE_DATE + " TEXT , "
                + "PRIMARY KEY(`codeqr`,`createdate`)" +

                ");");
    }

    private static void createTableReview(SQLiteDatabase db) {
        Log.d(TAG, "executing createTableReview");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + Review.TABLE_NAME + "("
                + Review.KEY + " TEXT, "
                + Review.CODE_QR + " TEXT, "
                + Review.VALUE + " TEXT, "
                + "PRIMARY KEY(`key`)" +

                ");");
    }

    static void createAllTable(SQLiteDatabase db) {
        createTableTokenQueue(db);
        createTableTokenQueueHistory(db);
        createTableReview(db);
    }
}
