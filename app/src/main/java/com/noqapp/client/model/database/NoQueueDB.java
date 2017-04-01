package com.noqapp.client.model.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.noqapp.client.presenter.beans.JsonTokenAndQueue;
import com.noqapp.client.presenter.interfaces.NOQueueDBPreseneterInterface;
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
    private static final String COLUMN_CODEQR = "codeqr";
    private static final String COLUMN_BUSINNESSNAME = "bussinessname";
    private static final String COLUMN_DISPLAYNAME = "displayname";
    private static final String COLUMN_STOREADDRESS = "storeaddress";
    private static final String COLUMN_STOREPHONE = "storephone";
    private static final String COLUMN_TOKENAVAILABLEFROM = "tokenavailablefrom";
    private static final String COLUMN_STARTHOUR = "starthour";
    private static final String COLUMN_ENDHOUR = "endhour";
    private static final String COLUMN_TOPIC = "topic";
    private static final String COLUMN_SERVINGNUMBER = "servingnumber";
    private static final String COLUMN_LASTNUMBER = "lastnumber";
    private static final String COLUMN_TOKEN = "token";
    private static final String COLUMN_QUEUESTATUS = "queuestatus";
    private static final String COLUMN_CREATEDATE = "createdate";

    public NOQueueDBPreseneterInterface queueDBPreseneterInterface;
    public SQLiteDatabase db;


    public NoQueueDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    public void save(List<JsonTokenAndQueue> list) {
        db = this.getWritableDatabase();
        for (JsonTokenAndQueue tokenAndQueue : list) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_CODEQR, tokenAndQueue.getCodeQR());
            values.put(COLUMN_BUSINNESSNAME, tokenAndQueue.getBusinessName());
            values.put(COLUMN_DISPLAYNAME, tokenAndQueue.getDisplayName());
            values.put(COLUMN_STOREADDRESS, tokenAndQueue.getStoreAddress());
            values.put(COLUMN_STOREPHONE, tokenAndQueue.getStorePhone());
            values.put(COLUMN_TOKENAVAILABLEFROM, tokenAndQueue.getTokenAvailableFrom());
            values.put(COLUMN_STARTHOUR, tokenAndQueue.getStartHour());
            values.put(COLUMN_ENDHOUR, tokenAndQueue.getEndHour());
            values.put(COLUMN_TOPIC, tokenAndQueue.getTopic());
            values.put(COLUMN_SERVINGNUMBER, tokenAndQueue.getServingNumber());
            values.put(COLUMN_LASTNUMBER, tokenAndQueue.getLastNumber());
            values.put(COLUMN_TOKEN, tokenAndQueue.getToken());
            values.put(COLUMN_QUEUESTATUS, tokenAndQueue.getQueueStatus().getName());
            values.put(COLUMN_CREATEDATE, tokenAndQueue.getCreateDate());
            long msg = db.insert(TOKEN_QUEUE, null, values);

            if (msg > 0) {
                Log.d(TAG, "DATA success saved " + String.valueOf(msg));
            }
        }

        queueDBPreseneterInterface.dbSaved("data successfully saved");


    }
}
