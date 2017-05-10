package com.noqapp.client.model.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import static com.noqapp.client.model.database.DatabaseTable.*;

/**
 * User: hitender
 * Date: 5/9/17 7:18 PM
 */

public class CreateTable {
    private static final String TAG = CreateTable.class.getSimpleName();

    private CreateTable() {
    }

    public static void createTableKeyValue(SQLiteDatabase db) {
        Log.d(TAG, "executing createTableKeyValue");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + KeyValue.TABLE_NAME + "("
                + KeyValue.KEY + " TEXT ,"
                + KeyValue.VALUE + " TEXT " +

                ");");
    }

    public static void createTableTokenQueue(SQLiteDatabase db) {
        Log.d(TAG, "executing createTableTokenQueue");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TokenQueue.TABLE_NAME + "("
                + TokenQueue.COLUMN_CODE_QR + " TEXT ,"
                + TokenQueue.COLUMN_BUSINESS_NAME + " TEXT ,"
                + TokenQueue.COLUMN_DISPLAY_NAME + " TEXT ,"
                + TokenQueue.COLUMN_STORE_ADDRESS + " TEXT ,"
                + TokenQueue.COLUMN_COUNTRY_SHORT_NAME + " TEXT ,"
                + TokenQueue.COLUMN_STORE_PHONE + " TEXT ,"
                + TokenQueue.COLUMN_TOKEN_AVAILABLE_FROM + " TEXT ,"
                + TokenQueue.COLUMN_START_HOUR + " TEXT ,"
                + TokenQueue.COLUMN_END_HOUR + " TEXT ,"
                + TokenQueue.COLUMN_TOPIC + " TEXT ,"
                + TokenQueue.COLUMN_SERVING_NUMBER + " TEXT ,"
                + TokenQueue.COLUMN_LAST_NUMBER + " TEXT ,"
                + TokenQueue.COLUMN_TOKEN + " TEXT ,"
                + TokenQueue.COLUMN_QUEUE_STATUS + " TEXT ,"
                + TokenQueue.COLUMN_SERVICED_TIME + " TEXT ,"
                + TokenQueue.COLUMN_CREATE_DATE + " TEXT ,"
                + "PRIMARY KEY(`codeqr`,`createdate`)" +

                ");");
    }

    public static void createTableTokenQueueHistory(SQLiteDatabase db) {
        Log.d(TAG, "executing createTableTokenQueue");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TokenQueueHistory.TABLE_NAME + "("
                + TokenQueue.COLUMN_CODE_QR + " TEXT ,"
                + TokenQueue.COLUMN_BUSINESS_NAME + " TEXT ,"
                + TokenQueue.COLUMN_DISPLAY_NAME + " TEXT ,"
                + TokenQueue.COLUMN_STORE_ADDRESS + " TEXT ,"
                + TokenQueue.COLUMN_COUNTRY_SHORT_NAME + " TEXT ,"
                + TokenQueue.COLUMN_STORE_PHONE + " TEXT ,"
                + TokenQueue.COLUMN_TOKEN_AVAILABLE_FROM + " TEXT ,"
                + TokenQueue.COLUMN_START_HOUR + " TEXT ,"
                + TokenQueue.COLUMN_END_HOUR + " TEXT ,"
                + TokenQueue.COLUMN_TOPIC + " TEXT ,"
                + TokenQueue.COLUMN_SERVING_NUMBER + " TEXT ,"
                + TokenQueue.COLUMN_LAST_NUMBER + " TEXT ,"
                + TokenQueue.COLUMN_TOKEN + " TEXT ,"
                + TokenQueue.COLUMN_QUEUE_STATUS + " TEXT ,"
                + TokenQueue.COLUMN_SERVICED_TIME + " TEXT ,"
                + TokenQueue.COLUMN_CREATE_DATE + " TEXT ,"
                + "PRIMARY KEY(`codeqr`,`createdate`)" +

                ");");
    }
}
