package com.noqapp.android.client.model.database;

import static com.noqapp.android.client.model.database.DatabaseTable.Notification;
import static com.noqapp.android.client.model.database.DatabaseTable.Review;
import static com.noqapp.android.client.model.database.DatabaseTable.TokenQueue;
import static com.noqapp.android.client.model.database.DatabaseTable.TokenQueueHistory;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * User: hitender
 * Date: 5/9/17 7:18 PM
 */

public class CreateTable {
    private static final String TAG = CreateTable.class.getSimpleName();

    private CreateTable() {
    }

    private static void createTableTokenQueue(SQLiteDatabase db) {
        try {
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
                    + TokenQueue.SERVICE_END_TIME + " TEXT , "
                    + TokenQueue.RATING_COUNT + " TEXT , "
                    + TokenQueue.HOURS_SAVED + " TEXT , "
                    + TokenQueue.CREATE_DATE + " TEXT , "
                    + TokenQueue.BUSINESS_TYPE + " TEXT , "
                    + TokenQueue.GEOHASH + " TEXT , "
                    + TokenQueue.TOWN + " TEXT , "
                    + TokenQueue.AREA + " TEXT , "
                    + TokenQueue.DISPLAY_IMAGE + " TEXT , "
                    + TokenQueue.QUEUE_USER_ID + " TEXT , "
                    + TokenQueue.PURCHASE_ORDER_STATE + " TEXT , "
                    + "PRIMARY KEY(`" + TokenQueue.CODE_QR + "`,`" + TokenQueue.TOKEN + "`,`" + TokenQueue.CREATE_DATE + "`)" +

                    ");");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void createTableTokenQueueHistory(SQLiteDatabase db) {
        Log.d(TAG, "executing createTableTokenQueue");
        try {


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
                    + TokenQueue.SERVICE_END_TIME + " TEXT , "
                    + TokenQueue.RATING_COUNT + " TEXT , "
                    + TokenQueue.HOURS_SAVED + " TEXT , "
                    + TokenQueue.CREATE_DATE + " TEXT , "
                    + TokenQueue.BUSINESS_TYPE + " TEXT , "
                    + TokenQueue.GEOHASH + " TEXT , "
                    + TokenQueue.TOWN + " TEXT , "
                    + TokenQueue.AREA + " TEXT , "
                    + TokenQueue.DISPLAY_IMAGE + " TEXT , "
                    + TokenQueue.QUEUE_USER_ID + " TEXT , "
                    + TokenQueue.PURCHASE_ORDER_STATE + " TEXT , "
                    + "PRIMARY KEY(`" + TokenQueue.CODE_QR + "`,`" + TokenQueue.TOKEN + "`,`" + TokenQueue.CREATE_DATE + "`)" +

                    ");");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void createTableReview(SQLiteDatabase db) {
        Log.d(TAG, "executing createTableReview");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + Review.TABLE_NAME + "("
                + Review.KEY + " TEXT, "
                + Review.CODE_QR + " TEXT, "
                + Review.VALUE + " TEXT, "
                + Review.TOKEN + " TEXT, "
                + Review.Q_USER_ID + " TEXT, "
                + "PRIMARY KEY(`" + Review.CODE_QR + "`,`" + Review.TOKEN + "`)" +

                ");");
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
                + Notification.CREATE_DATE + " TEXT, "
                + Notification.BUSINESS_TYPE + " TEXT " +
                //+ "PRIMARY KEY(`" + Notification.KEY + "`)" +

                ");");
    }

    static void alterTable(SQLiteDatabase db) {
        try {
            db.execSQL("ALTER TABLE " + TokenQueue.TABLE_NAME + " ADD COLUMN " + TokenQueue.BUSINESS_TYPE + " TEXT  ");
            db.execSQL("ALTER TABLE " + TokenQueue.TABLE_NAME + " ADD COLUMN " + TokenQueue.GEOHASH + " TEXT  ");
            db.execSQL("ALTER TABLE " + TokenQueue.TABLE_NAME + " ADD COLUMN " + TokenQueue.TOWN + " TEXT  ");
            db.execSQL("ALTER TABLE " + TokenQueue.TABLE_NAME + " ADD COLUMN " + TokenQueue.AREA + " TEXT  ");
            db.execSQL("ALTER TABLE " + TokenQueue.TABLE_NAME + " ADD COLUMN " + TokenQueue.DISPLAY_IMAGE + " TEXT  ");
            db.execSQL("ALTER TABLE " + TokenQueue.TABLE_NAME + " ADD COLUMN " + TokenQueue.QUEUE_USER_ID + " TEXT  ");

            db.execSQL("ALTER TABLE " + TokenQueueHistory.TABLE_NAME + " ADD COLUMN " + TokenQueue.BUSINESS_TYPE + " TEXT  ");
            db.execSQL("ALTER TABLE " + TokenQueueHistory.TABLE_NAME + " ADD COLUMN " + TokenQueue.GEOHASH + " TEXT  ");
            db.execSQL("ALTER TABLE " + TokenQueueHistory.TABLE_NAME + " ADD COLUMN " + TokenQueue.TOWN + " TEXT  ");
            db.execSQL("ALTER TABLE " + TokenQueueHistory.TABLE_NAME + " ADD COLUMN " + TokenQueue.AREA + " TEXT  ");
            db.execSQL("ALTER TABLE " + TokenQueueHistory.TABLE_NAME + " ADD COLUMN " + TokenQueue.DISPLAY_IMAGE + " TEXT  ");
            db.execSQL("ALTER TABLE " + TokenQueueHistory.TABLE_NAME + " ADD COLUMN " + TokenQueue.QUEUE_USER_ID + " TEXT  ");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void updateBusinessType(SQLiteDatabase db) {
        try {
            db.execSQL("UPDATE " + TokenQueue.TABLE_NAME + " SET " + TokenQueue.BUSINESS_TYPE + "= 'DO'  WHERE " + TokenQueue.BUSINESS_TYPE + " = 'HO'");
            db.execSQL("UPDATE " + TokenQueueHistory.TABLE_NAME + " SET " + TokenQueue.BUSINESS_TYPE + "= 'DO'  WHERE " + TokenQueue.BUSINESS_TYPE + " = 'HO'");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void dropAndCreateTable(SQLiteDatabase db) {

        db.execSQL("DROP TABLE IF EXISTS '" + TokenQueue.TABLE_NAME + "'");
        db.execSQL("DROP TABLE IF EXISTS '" + TokenQueueHistory.TABLE_NAME + "'");
        db.execSQL("DROP TABLE IF EXISTS '" + Review.TABLE_NAME + "'");
        db.execSQL("DROP TABLE IF EXISTS '" + Notification.TABLE_NAME + "'");
        createTableTokenQueue(db);
        createTableTokenQueueHistory(db);
        createTableReview(db);
        createTableNotification(db);
    }


    static void createAllTable(SQLiteDatabase db) {
        createTableTokenQueue(db);
        createTableTokenQueueHistory(db);
        createTableReview(db);
        createTableNotification(db);
    }

    public static void addOrderState(SQLiteDatabase db) {
        try {
            db.execSQL("ALTER TABLE " + TokenQueue.TABLE_NAME + " ADD COLUMN " + TokenQueue.PURCHASE_ORDER_STATE + " TEXT  ");
            db.execSQL("ALTER TABLE " + TokenQueueHistory.TABLE_NAME + " ADD COLUMN " + TokenQueue.PURCHASE_ORDER_STATE + " TEXT  ");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
