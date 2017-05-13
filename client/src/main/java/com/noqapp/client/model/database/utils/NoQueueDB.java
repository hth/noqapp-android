package com.noqapp.client.model.database.utils;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.noqapp.client.model.database.DatabaseTable.TokenQueueHistory;
import com.noqapp.client.presenter.beans.JsonTokenAndQueue;
import com.noqapp.client.presenter.interfaces.NOQueueDBPresenterInterface;

import java.util.ArrayList;
import java.util.List;

import static com.noqapp.client.model.database.DatabaseTable.TokenQueue;
import static com.noqapp.client.views.activities.LaunchActivity.RDH;

/**
 * Created by omkar on 4/1/17.
 */
public class NoQueueDB {
    private static final String TAG = NoQueueDB.class.getSimpleName();

    public static NOQueueDBPresenterInterface queueDBPresenterInterface;

    public static void save(List<JsonTokenAndQueue> list, boolean isCurrentQueueCall) {
        long msg = 0;
        for (JsonTokenAndQueue tokenAndQueue : list) {

            String tempTableName;
            ContentValues values = new ContentValues();
            values.put(TokenQueue.CODE_QR, tokenAndQueue.getCodeQR());
            values.put(TokenQueue.BUSINESS_NAME, tokenAndQueue.getBusinessName());
            values.put(TokenQueue.DISPLAY_NAME, tokenAndQueue.getDisplayName());
            values.put(TokenQueue.STORE_ADDRESS, tokenAndQueue.getStoreAddress());
            values.put(TokenQueue.COUNTRY_SHORT_NAME, tokenAndQueue.getCountryShortName());
            values.put(TokenQueue.STORE_PHONE, tokenAndQueue.getStorePhone());
            values.put(TokenQueue.TOKEN_AVAILABLE_FROM, tokenAndQueue.getTokenAvailableFrom());
            values.put(TokenQueue.START_HOUR, tokenAndQueue.getStartHour());
            values.put(TokenQueue.END_HOUR, tokenAndQueue.getEndHour());
            values.put(TokenQueue.TOPIC, tokenAndQueue.getTopic());
            values.put(TokenQueue.SERVING_NUMBER, tokenAndQueue.getServingNumber());
            values.put(TokenQueue.LAST_NUMBER, tokenAndQueue.getLastNumber());
            values.put(TokenQueue.TOKEN, tokenAndQueue.getToken());
            if (null != tokenAndQueue.getQueueStatus()) {
                values.put(TokenQueue.QUEUE_STATUS, tokenAndQueue.getQueueStatus().getName());
            }

            values.put(TokenQueue.SERVICED_TIME, tokenAndQueue.getServicedTime());
            values.put(TokenQueue.CREATE_DATE, tokenAndQueue.getCreateDate());
            try {
                if (isCurrentQueueCall) {
                    tempTableName = TokenQueue.TABLE_NAME;
                    RDH.getWritableDatabase().insertWithOnConflict(tempTableName, null, values, SQLiteDatabase.CONFLICT_REPLACE);

                } else {
                    tempTableName = TokenQueueHistory.TABLE_NAME;
                    RDH.getWritableDatabase().insertWithOnConflict(tempTableName, null, values, SQLiteDatabase.CONFLICT_REPLACE);

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
        String whereClause = TokenQueue.CODE_QR + " = ?" + " AND " + TokenQueue.CREATE_DATE + " = ?";
        return DatabaseUtils.longForQuery(
                RDH.getDb(),
                "SELECT COUNT(*) FROM " + table_name + " WHERE " + whereClause,
                new String[]{qrcode, date}) > 0;
    }

    public static void deleteTokenQueue(String codeQR) {
        boolean resultStatus = RDH.getReadableDatabase().delete(TokenQueue.TABLE_NAME, "codeqr=?", new String[]{codeQR}) > 0;
        Log.v("deleted", String.valueOf(resultStatus));
    }

    public static List<JsonTokenAndQueue> getCurrentQueueList() {
        List<JsonTokenAndQueue> listJsonQueue = new ArrayList<>();
        Cursor cursor = RDH.getReadableDatabase().query(true, TokenQueue.TABLE_NAME, null, null, null, null, null, TokenQueue.CREATE_DATE, null);

        if (cursor != null && cursor.getCount() > 0) {
            try {
                while (cursor.moveToNext()) {
                    JsonTokenAndQueue tokenAndQueue = new JsonTokenAndQueue();
                    tokenAndQueue.setCodeQR(cursor.getString(0));
                    tokenAndQueue.setBusinessName(cursor.getString(1));
                    tokenAndQueue.setDisplayName(cursor.getString(2));
                    tokenAndQueue.setStoreAddress(cursor.getString(3));
                    tokenAndQueue.setCountryShortName(cursor.getString(4));
                    tokenAndQueue.setStorePhone(cursor.getString(5));
                    tokenAndQueue.setTopic(cursor.getString(9));
                    tokenAndQueue.setToken(cursor.getInt(12));
                    tokenAndQueue.setServicedTime(cursor.getString(14));
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

    public static JsonTokenAndQueue getCurrentQueueObject(String codeQR) {
        JsonTokenAndQueue tokenAndQueue = null;
        Cursor cursor = RDH.getReadableDatabase().query(true, TokenQueue.TABLE_NAME, null, TokenQueue.CODE_QR + "=?", new String[]{codeQR}, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            try {
                while (cursor.moveToNext()) {
                    tokenAndQueue = new JsonTokenAndQueue();
                    tokenAndQueue.setCodeQR(cursor.getString(0));
                    tokenAndQueue.setBusinessName(cursor.getString(1));
                    tokenAndQueue.setDisplayName(cursor.getString(2));
                    tokenAndQueue.setStoreAddress(cursor.getString(3));
                    tokenAndQueue.setCountryShortName(cursor.getString(4));
                    tokenAndQueue.setStorePhone(cursor.getString(5));
                    tokenAndQueue.setTopic(cursor.getString(9));
                    tokenAndQueue.setToken(cursor.getInt(12));
                    tokenAndQueue.setServicedTime(cursor.getString(14));

                }
            } catch (Exception e) {
                Log.e(TAG, "Parsing error reason=" + e.getLocalizedMessage(), e);
            } finally {
                if (!cursor.isClosed()) {
                    cursor.close();
                }
            }
        }
        return tokenAndQueue;
    }

    public static List<JsonTokenAndQueue> getHistoryQueueList() {
        String orderBy = TokenQueue.CREATE_DATE;

        List<JsonTokenAndQueue> listJsonQueue = new ArrayList<>();
        Cursor cursor = RDH.getReadableDatabase().query(true, TokenQueueHistory.TABLE_NAME, null, null, null, null, null, orderBy, null);

        if (cursor != null) {
            if (cursor.getCount() > 0) {

                try {
                    while (cursor.moveToNext()) {
                        JsonTokenAndQueue tokenAndQueue = new JsonTokenAndQueue();
                        tokenAndQueue.setCodeQR(cursor.getString(0));
                        tokenAndQueue.setBusinessName(cursor.getString(1));
                        tokenAndQueue.setDisplayName(cursor.getString(2));
                        tokenAndQueue.setStoreAddress(cursor.getString(3));
                        tokenAndQueue.setStorePhone(cursor.getString(5));
                        tokenAndQueue.setTopic(cursor.getString(9));
                        tokenAndQueue.setToken(cursor.getInt(12));
                        tokenAndQueue.setServicedTime(cursor.getString(14));
                        listJsonQueue.add(tokenAndQueue);
                    }
                } finally {
                    cursor.close();
                }
            }
        }
        return listJsonQueue;
    }

    public static void deleteCurrentQueue() {
        RDH.getReadableDatabase().execSQL("delete from "+ TokenQueue.TABLE_NAME);
    }
}
