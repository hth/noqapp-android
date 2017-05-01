package com.noqapp.client.model.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.noqapp.client.presenter.beans.JsonTokenAndQueue;
import com.noqapp.client.presenter.interfaces.NOQueueDBPresenterInterface;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
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
    private static final String TABLE_TOKENQUEUE = "TOKEN_QUEUE";
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

    // History Token queue
    private static final String TABLE_TOKENQUEUE_H = "TOKEN_QUEUE_H";
    public NOQueueDBPresenterInterface queueDBPresenterInterface;
    private SQLiteDatabase db;

    public NoQueueDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void save(List<JsonTokenAndQueue> list, boolean isCurrentQueueCall) {
        db = this.getWritableDatabase();
        long msg = 0;
        for (JsonTokenAndQueue tokenAndQueue : list) {

            String tempTableName;
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
            if (null != tokenAndQueue.getQueueStatus()) {
                values.put(COLUMN_QUEUE_STATUS, tokenAndQueue.getQueueStatus().getName());

            }

            values.put(COLUMN_CREATE_DATE, tokenAndQueue.getCreateDate());
            try {
                if (isCurrentQueueCall) {
                    tempTableName = TABLE_TOKENQUEUE;
//                    if (isTokenExist(tempTableName,tokenAndQueue.getCodeQR(),tokenAndQueue.getCreateDate())) {
//                        String wherClause = COLUMN_CODE_QR+" = ?"+" AND "+COLUMN_CREATE_DATE+" = ?";
//                        values.remove(COLUMN_CODE_QR);
//                        values.remove(COLUMN_CREATE_DATE);
//
//                        db.update(tempTableName,values,wherClause,new String[]{tokenAndQueue.getCodeQR(),tokenAndQueue.getCreateDate()});
//                    } else {
//                        msg = db.insertOrThrow(TABLE_TOKENQUEUE, null, values);
//                    }
                    db.insertWithOnConflict(tempTableName, null, values, SQLiteDatabase.CONFLICT_REPLACE);

                } else {
                    tempTableName = TABLE_TOKENQUEUE_H;
//                    if (isTokenExist(tempTableName,tokenAndQueue.getCodeQR(),tokenAndQueue.getCreateDate())) {
//                        String wherClause = COLUMN_CODE_QR+" = ?"+" AND "+COLUMN_CREATE_DATE+" = ?";
//                        values.remove(COLUMN_CODE_QR);
//                        values.remove(COLUMN_CREATE_DATE);
//                        db.update(tempTableName,values,wherClause,new String[]{tokenAndQueue.getCodeQR(),tokenAndQueue.getCreateDate()});
//                    } else {
//                        msg = db.insertOrThrow(TABLE_TOKENQUEUE, null, values);
//                    }
                    db.insertWithOnConflict(tempTableName, null, values, SQLiteDatabase.CONFLICT_REPLACE);

                }

                if (msg > 0) {
                    Log.d(TAG, "Data Saved " + String.valueOf(msg));
                }

            } catch (SQLException e) {
                Log.e(TAG, "Exception ::" + e.getMessage().toString());
            }

        }
        queueDBPresenterInterface.dbSaved((int) msg);

    }

    public boolean isTokenExist(String table_name, String qrcode, String date) {
        String whereClause = COLUMN_CODE_QR + " = ?" + " AND " + COLUMN_CREATE_DATE + " = ?";
        return DatabaseUtils.longForQuery(
                db,
                "SELECT COUNT(*) FROM " + table_name + " WHERE " + whereClause,
                new String[]{qrcode, date}) > 0;
    }

    public void deleteTokenQueue(String codeQR) {
        db = this.getReadableDatabase();
        boolean resultStatus = db.delete(TABLE_TOKENQUEUE, "codeqr=?", new String[]{codeQR}) > 0;
        Log.v("deleted", String.valueOf(resultStatus));
    }

    public List<JsonTokenAndQueue> getCurrentQueueList() {
        db = this.getReadableDatabase();
        List<JsonTokenAndQueue> listJsonQueue = new ArrayList<>();
        Cursor cursor = db.query(true, TABLE_TOKENQUEUE, null, null, null, null, null, COLUMN_CREATE_DATE, null);
        if (cursor != null && cursor.getCount() > 0) {
            try {
                while (cursor.moveToNext()) {
                    JsonTokenAndQueue tokenAndQueue = new JsonTokenAndQueue();
                    tokenAndQueue.setCodeQR(cursor.getString(0));
                    tokenAndQueue.setBusinessName(cursor.getString(1));
                    tokenAndQueue.setDisplayName(cursor.getString(2));
                    tokenAndQueue.setStoreAddress(cursor.getString(3));
                    tokenAndQueue.setStorePhone(cursor.getString(4));
                    tokenAndQueue.setTopic(cursor.getString(8));
                    tokenAndQueue.setToken(cursor.getInt(11));
                    listJsonQueue.add(tokenAndQueue);
                }
            } catch (Exception e) {
                Log.e(TAG, "Parsing error reason=" + e.getLocalizedMessage(), e);
            } finally {
                if (!cursor.isClosed()) {
                    cursor.close();
                }
            }
        }
        return listJsonQueue;
    }

    public List<JsonTokenAndQueue> getHistoryQueueList() {
        db = this.getReadableDatabase();
        String[] columns = new String[]{COLUMN_BUSINESS_NAME, COLUMN_CODE_QR, COLUMN_STORE_ADDRESS, COLUMN_STORE_PHONE, COLUMN_TOKEN};
        String whereClause = COLUMN_CODE_QR + " = ? and " + COLUMN_CREATE_DATE + " = ?";
        //String [] selectionArgs = new String[] {codeQR,dateTime};
        String orderBy = COLUMN_CREATE_DATE;

        List<JsonTokenAndQueue> listJsonQueue = new ArrayList<>();
        Cursor cursor = db.query(true, TABLE_TOKENQUEUE_H, null, null, null, null, null, orderBy, null);

        if (cursor != null) {
            if (cursor.getCount() > 0) {

                try {
                    while (cursor.moveToNext()) {
                        JsonTokenAndQueue tokenAndQueue = new JsonTokenAndQueue();
                        tokenAndQueue.setCodeQR(cursor.getString(0));
                        tokenAndQueue.setBusinessName(cursor.getString(1));
                        tokenAndQueue.setDisplayName(cursor.getString(2));
                        tokenAndQueue.setStoreAddress(cursor.getString(3));
                        tokenAndQueue.setStorePhone(cursor.getString(4));
                        tokenAndQueue.setTopic(cursor.getString(8));
                        tokenAndQueue.setToken(cursor.getInt(11));
                        listJsonQueue.add(tokenAndQueue);

                    }
                } finally {
                    cursor.close();
                }
            }
        }
        return listJsonQueue;
    }
}
