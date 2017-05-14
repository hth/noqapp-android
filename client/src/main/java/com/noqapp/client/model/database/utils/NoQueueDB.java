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
import static com.noqapp.client.views.activities.LaunchActivity.dbHandler;

/**
 * Created by omkar on 4/1/17.
 */
public class NoQueueDB {
    private static final String TAG = NoQueueDB.class.getSimpleName();

    public static NOQueueDBPresenterInterface queueDBPresenterInterface;

    public boolean isTokenExist(String table_name, String qrcode, String date) {
        String whereClause = TokenQueue.CODE_QR + " = ?" + " AND " + TokenQueue.CREATE_DATE + " = ?";
        return DatabaseUtils.longForQuery(
                dbHandler.getWritableDb(),
                "SELECT COUNT(*) FROM " + table_name + " WHERE " + whereClause,
                new String[]{qrcode, date}) > 0;
    }

    public static void deleteTokenQueue(String codeQR) {
        boolean resultStatus = dbHandler.getReadableDatabase().delete(TokenQueue.TABLE_NAME, "codeqr=?", new String[]{codeQR}) > 0;
        Log.v("deleted", String.valueOf(resultStatus));
    }

    public static List<JsonTokenAndQueue> getCurrentQueueList() {
        List<JsonTokenAndQueue> listJsonQueue = new ArrayList<>();
        Cursor cursor = dbHandler.getReadableDatabase().query(true, TokenQueue.TABLE_NAME, null, null, null, null, null, TokenQueue.CREATE_DATE, null);

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
        Cursor cursor = dbHandler.getReadableDatabase().query(true, TokenQueue.TABLE_NAME, null, TokenQueue.CODE_QR + "=?", new String[]{codeQR}, null, null, null, null);
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
        Cursor cursor = dbHandler.getReadableDatabase().query(true, TokenQueueHistory.TABLE_NAME, null, null, null, null, null, orderBy, null);

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
        dbHandler.getWritableDb().execSQL("delete from "+ TokenQueue.TABLE_NAME);
    }

    public static void saveCurrentQueue(List<JsonTokenAndQueue> list) {
        long msg = 0;
        for (JsonTokenAndQueue tokenAndQueue : list) {
            ContentValues values = createQueueContentValues(tokenAndQueue);
            try {
                long succcesscount =dbHandler.getWritableDb().insertWithOnConflict(TokenQueue.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                Log.d(TAG, "Data Saved current queue " + String.valueOf(succcesscount));
                if (msg > 0) {
                    Log.d(TAG, "Data Saved " + String.valueOf(msg));
                }
            } catch (SQLException e) {
                Log.e(TAG, "Exception ::" + e.getMessage().toString());
            }
        }
        queueDBPresenterInterface.dbSaved(true);
    }

    public static void saveHistoryQueue(List<JsonTokenAndQueue> list) {
        long msg = 0;
        for (JsonTokenAndQueue tokenAndQueue : list) {
            ContentValues values = createQueueContentValues(tokenAndQueue);
            try {
                long succcesscount= dbHandler.getWritableDb().insertWithOnConflict(TokenQueueHistory.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                Log.d(TAG, "Data Saved hsitory queue " + String.valueOf(succcesscount));
                if (msg > 0) {
                    Log.d(TAG, "Data Saved " + String.valueOf(msg));
                }
            } catch (SQLException e) {
                Log.e(TAG, "Exception ::" + e.getMessage().toString());
            }
        }
        queueDBPresenterInterface.dbSaved(false);
    }
    private static ContentValues createQueueContentValues(JsonTokenAndQueue tokenAndQueue ){
        ContentValues cv = new ContentValues();
        try {
            cv.put(TokenQueue.CODE_QR, tokenAndQueue.getCodeQR());
            cv.put(TokenQueue.BUSINESS_NAME, tokenAndQueue.getBusinessName());
            cv.put(TokenQueue.DISPLAY_NAME, tokenAndQueue.getDisplayName());
            cv.put(TokenQueue.STORE_ADDRESS, tokenAndQueue.getStoreAddress());
            cv.put(TokenQueue.COUNTRY_SHORT_NAME, tokenAndQueue.getCountryShortName());
            cv.put(TokenQueue.STORE_PHONE, tokenAndQueue.getStorePhone());
            cv.put(TokenQueue.TOKEN_AVAILABLE_FROM, tokenAndQueue.getTokenAvailableFrom());
            cv.put(TokenQueue.START_HOUR, tokenAndQueue.getStartHour());
            cv.put(TokenQueue.END_HOUR, tokenAndQueue.getEndHour());
            cv.put(TokenQueue.TOPIC, tokenAndQueue.getTopic());
            cv.put(TokenQueue.SERVING_NUMBER, tokenAndQueue.getServingNumber());
            cv.put(TokenQueue.LAST_NUMBER, tokenAndQueue.getLastNumber());
            cv.put(TokenQueue.TOKEN, tokenAndQueue.getToken());
            if (null != tokenAndQueue.getQueueStatus()) {
                cv.put(TokenQueue.QUEUE_STATUS, tokenAndQueue.getQueueStatus().getName());
            }

            cv.put(TokenQueue.SERVICED_TIME, tokenAndQueue.getServicedTime());
            cv.put(TokenQueue.CREATE_DATE, tokenAndQueue.getCreateDate());
        }catch (Exception e){
            e.printStackTrace();

        }
        return cv;
    }



}
