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
            values.put(TokenQueue.COLUMN_CODE_QR, tokenAndQueue.getCodeQR());
            values.put(TokenQueue.COLUMN_BUSINESS_NAME, tokenAndQueue.getBusinessName());
            values.put(TokenQueue.COLUMN_DISPLAY_NAME, tokenAndQueue.getDisplayName());
            values.put(TokenQueue.COLUMN_STORE_ADDRESS, tokenAndQueue.getStoreAddress());
            values.put(TokenQueue.COLUMN_COUNTRY_SHORT_NAME, tokenAndQueue.getCountryShortName());
            values.put(TokenQueue.COLUMN_STORE_PHONE, tokenAndQueue.getStorePhone());
            values.put(TokenQueue.COLUMN_TOKEN_AVAILABLE_FROM, tokenAndQueue.getTokenAvailableFrom());
            values.put(TokenQueue.COLUMN_START_HOUR, tokenAndQueue.getStartHour());
            values.put(TokenQueue.COLUMN_END_HOUR, tokenAndQueue.getEndHour());
            values.put(TokenQueue.COLUMN_TOPIC, tokenAndQueue.getTopic());
            values.put(TokenQueue.COLUMN_SERVING_NUMBER, tokenAndQueue.getServingNumber());
            values.put(TokenQueue.COLUMN_LAST_NUMBER, tokenAndQueue.getLastNumber());
            values.put(TokenQueue.COLUMN_TOKEN, tokenAndQueue.getToken());
            if (null != tokenAndQueue.getQueueStatus()) {
                values.put(TokenQueue.COLUMN_QUEUE_STATUS, tokenAndQueue.getQueueStatus().getName());
            }

            values.put(TokenQueue.COLUMN_SERVICED_TIME, tokenAndQueue.getServicedTime());
            values.put(TokenQueue.COLUMN_CREATE_DATE, tokenAndQueue.getCreateDate());
            try {
                if (isCurrentQueueCall) {
                    tempTableName = TokenQueue.TABLE_NAME;
//                    if (isTokenExist(tempTableName,tokenAndQueue.getCodeQR(),tokenAndQueue.getCreateDate())) {
//                        String wherClause = COLUMN_CODE_QR+" = ?"+" AND "+COLUMN_CREATE_DATE+" = ?";
//                        values.remove(COLUMN_CODE_QR);
//                        values.remove(COLUMN_CREATE_DATE);
//
//                        db.update(tempTableName,values,wherClause,new String[]{tokenAndQueue.getCodeQR(),tokenAndQueue.getCreateDate()});
//                    } else {
//                        msg = db.insertOrThrow(TABLE_TOKENQUEUE, null, values);
//                    }
                    RDH.getWritableDatabase().insertWithOnConflict(tempTableName, null, values, SQLiteDatabase.CONFLICT_REPLACE);

                } else {
                    tempTableName = TokenQueueHistory.TABLE_NAME;
//                    if (isTokenExist(tempTableName,tokenAndQueue.getCodeQR(),tokenAndQueue.getCreateDate())) {
//                        String wherClause = COLUMN_CODE_QR+" = ?"+" AND "+COLUMN_CREATE_DATE+" = ?";
//                        values.remove(COLUMN_CODE_QR);
//                        values.remove(COLUMN_CREATE_DATE);
//                        db.update(tempTableName,values,wherClause,new String[]{tokenAndQueue.getCodeQR(),tokenAndQueue.getCreateDate()});
//                    } else {
//                        msg = db.insertOrThrow(TABLE_TOKENQUEUE, null, values);
//                    }
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
        String whereClause = TokenQueue.COLUMN_CODE_QR + " = ?" + " AND " + TokenQueue.COLUMN_CREATE_DATE + " = ?";
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
        Cursor cursor = RDH.getReadableDatabase().query(true, TokenQueue.TABLE_NAME, null, null, null, null, null, TokenQueue.COLUMN_CREATE_DATE, null);

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
        // Cursor cursor = db.query(true, TABLE_TOKENQUEUE, new String[]{COLUMN_CODE_QR}, codeQR, null, null, null, null, null);
        ///  Cursor cursor = db.query(true,TABLE_TOKENQUEUE, null, "codeqr=?", new String[] { codeQR }, null, null,COLUMN_CREATE_DATE, null);
        Cursor cursor = RDH.getReadableDatabase().query(true, TokenQueue.TABLE_NAME, null, TokenQueue.COLUMN_CODE_QR + "=?", new String[]{codeQR}, null, null, null, null);
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
        String[] columns = new String[]{TokenQueue.COLUMN_BUSINESS_NAME, TokenQueue.COLUMN_CODE_QR, TokenQueue.COLUMN_STORE_ADDRESS, TokenQueue.COLUMN_STORE_PHONE, TokenQueue.COLUMN_TOKEN};
        String whereClause = TokenQueue.COLUMN_CODE_QR + " = ? and " + TokenQueue.COLUMN_CREATE_DATE + " = ?";
        //String [] selectionArgs = new String[] {codeQR,dateTime};
        String orderBy = TokenQueue.COLUMN_CREATE_DATE;

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
}
