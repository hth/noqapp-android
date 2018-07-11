package com.noqapp.android.client.model.database.utils;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.noqapp.android.client.model.database.DatabaseTable.TokenQueueHistory;
import com.noqapp.android.client.model.types.QueueStatusEnum;
import com.noqapp.android.client.presenter.beans.JsonTokenAndQueue;
import com.noqapp.android.client.presenter.interfaces.NOQueueDBPresenterInterface;
import com.noqapp.android.common.model.types.BusinessTypeEnum;

import java.util.ArrayList;
import java.util.List;

import static com.noqapp.android.client.model.database.DatabaseTable.TokenQueue;
import static com.noqapp.android.client.views.activities.LaunchActivity.dbHandler;

/**
 * User: omkar
 * Date: 4/1/17 11:40 AM
 */
public class TokenAndQueueDB {
    private static final String TAG = TokenAndQueueDB.class.getSimpleName();

    public static NOQueueDBPresenterInterface queueDBPresenterInterface;

    public static void deleteTokenQueue(String codeQR) {
        boolean resultStatus = dbHandler.getReadableDatabase().delete(TokenQueue.TABLE_NAME, TokenQueue.CODE_QR + "=?", new String[]{codeQR}) > 0;
        Log.i(TAG, "Deleted deleteTokenQueue status=" + String.valueOf(resultStatus));
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
                    tokenAndQueue.setTokenAvailableFrom(cursor.getInt(6));
                    tokenAndQueue.setStartHour(cursor.getInt(7));
                    tokenAndQueue.setEndHour(cursor.getInt(8));
                    tokenAndQueue.setTopic(cursor.getString(9));
                    tokenAndQueue.setServingNumber(cursor.getInt(10));
                    tokenAndQueue.setLastNumber(cursor.getInt(11));
                    tokenAndQueue.setToken(cursor.getInt(12));
                    tokenAndQueue.setQueueStatus(QueueStatusEnum.valueOf(cursor.getString(13)));
                    //    tokenAndQueue.setServiceEndTime(cursor.getString(14));
                    tokenAndQueue.setCreateDate(cursor.getString(15));
                    tokenAndQueue.setBusinessType(BusinessTypeEnum.valueOf(cursor.getString(18)));
                    tokenAndQueue.setGeoHash(cursor.getString(19));
                    tokenAndQueue.setTown(cursor.getString(20));
                    tokenAndQueue.setArea(cursor.getString(21));
                    tokenAndQueue.setDisplayImage(cursor.getString(22));
                    tokenAndQueue.setQueueUserId(cursor.getString(23));
                    listJsonQueue.add(tokenAndQueue);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error getCurrentQueueList reason=" + e.getLocalizedMessage(), e);
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
        try {
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
                        tokenAndQueue.setTokenAvailableFrom(cursor.getInt(6));
                        tokenAndQueue.setStartHour(cursor.getInt(7));
                        tokenAndQueue.setEndHour(cursor.getInt(8));
                        tokenAndQueue.setTopic(cursor.getString(9));
                        tokenAndQueue.setServingNumber(cursor.getInt(10));
                        tokenAndQueue.setLastNumber(cursor.getInt(11));
                        tokenAndQueue.setToken(cursor.getInt(12));
                        tokenAndQueue.setQueueStatus(QueueStatusEnum.valueOf(cursor.getString(13)));
                        //  tokenAndQueue.setServiceEndTime(cursor.getString(14));
                        //  tokenAndQueue.setRatingCount(cursor.getInt(15));
                        //  tokenAndQueue.setHoursSaved(cursor.getInt(16));
                        tokenAndQueue.setCreateDate(cursor.getString(17));
                        tokenAndQueue.setBusinessType(BusinessTypeEnum.valueOf(cursor.getString(18)));
                        tokenAndQueue.setGeoHash(cursor.getString(19));
                        tokenAndQueue.setTown(cursor.getString(20));
                        tokenAndQueue.setArea(cursor.getString(21));
                        tokenAndQueue.setDisplayImage(cursor.getString(22));
                        tokenAndQueue.setQueueUserId(cursor.getString(23));
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error getCurrentQueueObject reason=" + e.getLocalizedMessage(), e);
                } finally {
                    if (!cursor.isClosed()) {
                        cursor.close();
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error dbHandler currentQueue reason=" + e.getLocalizedMessage(), e);
        }
        return tokenAndQueue;
    }

    public static JsonTokenAndQueue getHistoryQueueObject(String codeQR) {
        JsonTokenAndQueue tokenAndQueue = null;
        Cursor cursor = dbHandler.getReadableDatabase().query(true, TokenQueueHistory.TABLE_NAME, null, TokenQueue.CODE_QR + "=?", new String[]{codeQR}, null, null, TokenQueue.CREATE_DATE, null);
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
                    tokenAndQueue.setTokenAvailableFrom(cursor.getInt(6));
                    tokenAndQueue.setStartHour(cursor.getInt(7));
                    tokenAndQueue.setEndHour(cursor.getInt(8));
                    tokenAndQueue.setTopic(cursor.getString(9));
                    tokenAndQueue.setServingNumber(cursor.getInt(10));
                    tokenAndQueue.setLastNumber(cursor.getInt(11));
                    tokenAndQueue.setToken(cursor.getInt(12));
                    //   tokenAndQueue.setQueueStatus(QueueStatusEnum.valueOf(cursor.getString(13)));
                    tokenAndQueue.setServiceEndTime(cursor.getString(14));
                    tokenAndQueue.setRatingCount(cursor.getInt(15));
                    tokenAndQueue.setHoursSaved(cursor.getInt(16));
                    tokenAndQueue.setCreateDate(cursor.getString(17));
                    tokenAndQueue.setBusinessType(BusinessTypeEnum.valueOf(cursor.getString(18)));
                    tokenAndQueue.setGeoHash(cursor.getString(19));
                    tokenAndQueue.setTown(cursor.getString(20));
                    tokenAndQueue.setArea(cursor.getString(21));
                    tokenAndQueue.setDisplayImage(cursor.getString(22));
                    tokenAndQueue.setQueueUserId(cursor.getString(23));
                }
            } catch (Exception e) {
                Log.e(TAG, "Error getCurrentQueueObject reason=" + e.getLocalizedMessage(), e);
            } finally {
                if (!cursor.isClosed()) {
                    cursor.close();
                }
            }
        }
        return tokenAndQueue;
    }

    public static List<JsonTokenAndQueue> getHistoryQueueList() {

        String query = "SELECT * , MAX(" + TokenQueue.CREATE_DATE + ") FROM " + TokenQueueHistory.TABLE_NAME + " GROUP BY " + TokenQueue.CODE_QR;
        List<JsonTokenAndQueue> listJsonQueue = new ArrayList<>();
        Cursor cursor = dbHandler.getWritableDb().rawQuery(query, null);

        if (cursor != null) {
            if (cursor.getCount() > 0) {

                try {
                    while (cursor.moveToNext()) {
                        JsonTokenAndQueue tokenAndQueue = new JsonTokenAndQueue();
                        tokenAndQueue.setCodeQR(cursor.getString(0));
                        tokenAndQueue.setBusinessName(cursor.getString(1));
                        tokenAndQueue.setDisplayName(cursor.getString(2));
                        tokenAndQueue.setStoreAddress(cursor.getString(3));
                        tokenAndQueue.setCountryShortName(cursor.getString(4));
                        tokenAndQueue.setStorePhone(cursor.getString(5));
                        tokenAndQueue.setTokenAvailableFrom(cursor.getInt(6));
                        tokenAndQueue.setStartHour(cursor.getInt(7));
                        tokenAndQueue.setEndHour(cursor.getInt(8));
                        tokenAndQueue.setTopic(cursor.getString(9));
                        tokenAndQueue.setServingNumber(cursor.getInt(10));
                        tokenAndQueue.setLastNumber(cursor.getInt(11));
                        tokenAndQueue.setToken(cursor.getInt(12));
                        // tokenAndQueue.setQueueStatus(QueueStatusEnum.valueOf(cursor.getString(13)));
                        tokenAndQueue.setServiceEndTime(cursor.getString(14));
                        tokenAndQueue.setRatingCount(cursor.getInt(15));
                        tokenAndQueue.setHoursSaved(cursor.getInt(16));
                        tokenAndQueue.setCreateDate(cursor.getString(17));
                        tokenAndQueue.setBusinessType(BusinessTypeEnum.valueOf(cursor.getString(18)));
                        tokenAndQueue.setGeoHash(cursor.getString(19));
                        tokenAndQueue.setTown(cursor.getString(20));
                        tokenAndQueue.setArea(cursor.getString(21));
                        tokenAndQueue.setDisplayImage(cursor.getString(22));
                        tokenAndQueue.setQueueUserId(cursor.getString(23));
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
        dbHandler.getWritableDb().execSQL("delete from " + TokenQueue.TABLE_NAME);
    }

    public static void saveCurrentQueue(List<JsonTokenAndQueue> list) {

        for (JsonTokenAndQueue tokenAndQueue : list) {
            ContentValues values = createQueueContentValues(tokenAndQueue);
            try {
                long successCount = dbHandler.getWritableDb().insertWithOnConflict(TokenQueue.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                Log.d(TAG, "Data Saved current queue " + String.valueOf(successCount));
            } catch (SQLException e) {
                Log.e(TAG, "Error saveCurrentQueue reason=" + e.getLocalizedMessage(), e);
            }
        }
        queueDBPresenterInterface.dbSaved(true);
    }

    public static void deleteHistoryQueue() {
        dbHandler.getWritableDb().execSQL("delete from " + TokenQueueHistory.TABLE_NAME);
    }

    public static void saveHistoryQueue(List<JsonTokenAndQueue> list) {
        for (JsonTokenAndQueue tokenAndQueue : list) {
            ContentValues values = createQueueContentValues(tokenAndQueue);
            try {
                long successCount = dbHandler.getWritableDb().insertWithOnConflict(TokenQueueHistory.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                Log.d(TAG, "Data Saved history queue " + String.valueOf(successCount));
            } catch (SQLException e) {
                Log.e(TAG, "Error saveHistoryQueue reason=" + e.getLocalizedMessage(), e);
            }
        }
        queueDBPresenterInterface.dbSaved(false);
    }

    private static ContentValues createQueueContentValues(JsonTokenAndQueue tokenAndQueue) {
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
            cv.put(TokenQueue.SERVICE_END_TIME, tokenAndQueue.getServiceEndTime());
            cv.put(TokenQueue.RATING_COUNT, tokenAndQueue.getRatingCount());
            cv.put(TokenQueue.HOURS_SAVED, tokenAndQueue.getHoursSaved());
            cv.put(TokenQueue.CREATE_DATE, tokenAndQueue.getCreateDate());
            cv.put(TokenQueue.AREA, tokenAndQueue.getArea());
            cv.put(TokenQueue.TOWN, tokenAndQueue.getTown());
            cv.put(TokenQueue.BUSINESS_TYPE, tokenAndQueue.getBusinessType().name());
            cv.put(TokenQueue.GEOHASH, tokenAndQueue.getGeoHash());
            cv.put(TokenQueue.DISPLAY_IMAGE, tokenAndQueue.getDisplayImage());
            cv.put(TokenQueue.QUEUE_USER_ID, tokenAndQueue.getQueueUserId());
        } catch (Exception e) {
            Log.e(TAG, "Error createQueueContentValue reason=" + e.getLocalizedMessage(), e);
        }
        return cv;
    }

    public static void saveJoinQueueObject(JsonTokenAndQueue object) {
        ContentValues values = createQueueContentValues(object);
        try {
            long rowInsertCount = dbHandler.getWritableDb().insertWithOnConflict(TokenQueue.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            Log.d(TAG, "Data Saved in current queue " + String.valueOf(rowInsertCount));
        } catch (SQLException e) {
            Log.e(TAG, "Error saveJoinQueueObject reason=" + e.getLocalizedMessage(), e);
        }
    }

    public static void updateJoinQueueObject(String codeQR, String servingno, String token) {
        try {
            ContentValues con = new ContentValues();
            con.put(TokenQueue.SERVING_NUMBER, servingno);
            con.put(TokenQueue.TOKEN, token);
            int successCount = dbHandler.getWritableDb().update(TokenQueue.TABLE_NAME, con, TokenQueue.CODE_QR + "=?", new String[]{codeQR});
            Log.d(TAG, "Data Saved " + TokenQueue.TABLE_NAME + " queue " + String.valueOf(successCount));
            //AppUtilities.exportDatabase(LaunchActivity.getLaunchActivity());
        } catch (Exception e) {
            Log.e(TAG, "Error updateJoinQueueObject reason=" + e.getLocalizedMessage(), e);
        }
    }

    public static boolean updateCurrentListQueueObject(String codeQR, String servingno, String token) {
        try {
            ContentValues con = new ContentValues();
            con.put(TokenQueue.SERVING_NUMBER, servingno);
            //  con.put(TokenQueue.TOKEN, token);
            int successCount = dbHandler.getWritableDb().update(TokenQueue.TABLE_NAME, con, TokenQueue.CODE_QR + "=?", new String[]{codeQR});
            Log.d(TAG, "Data Saved " + TokenQueue.TABLE_NAME + " queue " + String.valueOf(successCount));

            return successCount > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error updateJoinQueueObject reason=" + e.getLocalizedMessage(), e);
            return false;
        }
    }

    public boolean isTokenExist(String table_name, String codeQR, String date) {
        String whereClause = TokenQueue.CODE_QR + " = ?" + " AND " + TokenQueue.CREATE_DATE + " = ?";
        return DatabaseUtils.longForQuery(
                dbHandler.getWritableDb(),
                "SELECT COUNT(*) FROM " + table_name + " WHERE " + whereClause,
                new String[]{codeQR, date}) > 0;
    }
}

