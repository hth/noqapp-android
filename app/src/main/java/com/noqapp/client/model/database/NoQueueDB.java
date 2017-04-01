package com.noqapp.client.model.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.noqapp.client.presenter.beans.JsonTokenAndQueue;
import com.noqapp.client.presenter.interfaces.NOQueueDBPresenterInterface;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.List;

/**
 * Created by omkar on 4/1/17.
 */

public class NoQueueDB extends SQLiteAssetHelper {


    private static final String TAG = NoQueueDB.class.getSimpleName();
    private static final String DATABASE_NAME = "noqueue.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TOKEN_QUEUE = "TOKEN_QUEUE";

    //QUEUE_TOKEN column names
    private static final String COLUMN_CODE_QR = "codeqr";
    private static final String COLUMN_BUSINESS_NAME = "bussinessname";
    private static final String COLUMN_DISPLAY_NAME = "displayname";
    private static final String COLUMN_STORE_ADDRESS = "storeaddress";
    private static final String COLUMN_STORE_PHONE = "storephone";
    private static final String COLUMN_TOKEN_AVAILABLE_FROM = "tokenavailablefrom";
    private static final String COLUMN_START_HOUR = "starthour";
    private static final String COLUMN_END_HOUR = "endhour";
    private static final String COLUMN_TOPIC = "topic";
    private static final String COLUMN_SERVING_NUMBER = "servingnumber";
    private static final String COLUMN_LAST_NUMBER = "lastnumber";
    private static final String COLUMN_TOKEN = "token";
    private static final String COLUMN_QUEUE_STATUS = "queuestatus";
    private static final String COLUMN_CREATE_DATE = "createdate";

    public NOQueueDBPresenterInterface queueDBPresenterInterface;
    public SQLiteDatabase db;


    public NoQueueDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    public void save(List<JsonTokenAndQueue> list) {
        db = this.getWritableDatabase();
        for (JsonTokenAndQueue tokenAndQueue : list) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_CODE_QR, tokenAndQueue.getCodeQR());
            values.put(COLUMN_BUSINESS_NAME, tokenAndQueue.getBusinessName());
            values.put(COLUMN_DISPLAY_NAME, tokenAndQueue.getDisplayName());
            values.put(COLUMN_STORE_ADDRESS, tokenAndQueue.getStoreAddress());
            values.put(COLUMN_STORE_PHONE, tokenAndQueue.getStorePhone());
            values.put(COLUMN_TOKEN_AVAILABLE_FROM, tokenAndQueue.getTokenAvailableFrom());
            values.put(COLUMN_START_HOUR, tokenAndQueue.getStartHour());
            values.put(COLUMN_END_HOUR, tokenAndQueue.getEndHour());
            values.put(COLUMN_TOPIC, tokenAndQueue.getTopic());
            values.put(COLUMN_SERVING_NUMBER, tokenAndQueue.getServingNumber());
            values.put(COLUMN_LAST_NUMBER, tokenAndQueue.getLastNumber());
            values.put(COLUMN_TOKEN, tokenAndQueue.getToken());
            values.put(COLUMN_QUEUE_STATUS, tokenAndQueue.getQueueStatus().getName());
            values.put(COLUMN_CREATE_DATE, tokenAndQueue.getCreateDate());
            long msg = db.insert(TOKEN_QUEUE, null, values);

            if (msg > 0) {
                Log.d(TAG, "DATA success saved " + String.valueOf(msg));
            }
        }

        queueDBPresenterInterface.dbSaved("data successfully saved");


    }
}
