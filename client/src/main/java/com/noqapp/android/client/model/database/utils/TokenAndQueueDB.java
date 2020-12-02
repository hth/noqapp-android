package com.noqapp.android.client.model.database.utils;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.noqapp.android.client.model.database.DatabaseTable.TokenQueueHistory;
import com.noqapp.android.client.presenter.beans.JsonTokenAndQueue;
import com.noqapp.android.common.model.types.BusinessTypeEnum;
import com.noqapp.android.common.model.types.QueueStatusEnum;
import com.noqapp.android.common.model.types.order.PurchaseOrderStateEnum;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static com.noqapp.android.client.model.database.DatabaseTable.TokenQueue;
import static com.noqapp.android.client.views.activities.AppInitialize.dbHandler;

/**
 * User: omkar
 * Date: 4/1/17 11:40 AM
 */
public class TokenAndQueueDB {
    private static final String TAG = TokenAndQueueDB.class.getSimpleName();

    public static void deleteTokenQueue(String codeQR, String token) {
        boolean resultStatus = dbHandler.getReadableDatabase().delete(TokenQueue.TABLE_NAME, TokenQueue.CODE_QR + "=?" + " AND " + TokenQueue.TOKEN + " = ?", new String[]{codeQR, token}) > 0;
        Log.i(TAG, "Deleted deleteTokenQueue status=" + resultStatus);
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
                    tokenAndQueue.setDisplayServingNumber(cursor.getString(11));
                    tokenAndQueue.setLastNumber(cursor.getInt(12));
                    tokenAndQueue.setToken(cursor.getInt(13));
                    tokenAndQueue.setDisplayToken(cursor.getString(14));
                    tokenAndQueue.setQueueStatus(QueueStatusEnum.valueOf(cursor.getString(15)));
                    tokenAndQueue.setServiceEndTime(cursor.getString(16));
                    tokenAndQueue.setRatingCount(cursor.getInt(17));
                    tokenAndQueue.setAverageServiceTime(cursor.getInt(18));
                    tokenAndQueue.setHoursSaved(cursor.getInt(19));
                    tokenAndQueue.setCreateDate(cursor.getString(20));
                    tokenAndQueue.setBusinessType(cursor.getString(21) == null ? null : BusinessTypeEnum.valueOf(cursor.getString(21)));
                    tokenAndQueue.setGeoHash(cursor.getString(22));
                    tokenAndQueue.setTown(cursor.getString(23));
                    tokenAndQueue.setArea(cursor.getString(24));
                    tokenAndQueue.setDisplayImage(cursor.getString(25));
                    tokenAndQueue.setQueueUserId(cursor.getString(26));
                    tokenAndQueue.setPurchaseOrderState(PurchaseOrderStateEnum.valueOf(cursor.getString(27)));
                    tokenAndQueue.setTransactionId(cursor.getString(28));
                    tokenAndQueue.setTimeSlotMessage(cursor.getString(29));
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

    /** Added to update the current list with old values. */
    public static List<JsonTokenAndQueue> getUpdatedCurrentQueueList(List<JsonTokenAndQueue> newList, List<JsonTokenAndQueue> oldList) {
        if (null != newList && newList.size() != 0 && null != oldList && oldList.size() != 0) {
            for (int i = 0; i < newList.size(); i++) {
                JsonTokenAndQueue jtkNew = newList.get(i);
                for (int j = 0; j < oldList.size(); j++) {
                    JsonTokenAndQueue jtkOld = oldList.get(j);
                    if (jtkNew.getCodeQR().equals(jtkOld.getCodeQR()) && jtkNew.getToken() == jtkOld.getToken()) {
                        newList.get(i).setServiceEndTime(jtkOld.getServiceEndTime());
                        break;
                    }
                }

            }
        }
        return newList;
    }

    public static JsonTokenAndQueue getCurrentQueueObject(String codeQR, String token) {
        JsonTokenAndQueue tokenAndQueue = null;
        try {
            Cursor cursor = dbHandler.getReadableDatabase().query(true, TokenQueue.TABLE_NAME, null, TokenQueue.CODE_QR + "=?" + " AND " + TokenQueue.TOKEN + " = ?", new String[]{codeQR, token}, null, null, null, null);
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
                        tokenAndQueue.setDisplayServingNumber(cursor.getString(11));
                        tokenAndQueue.setLastNumber(cursor.getInt(12));
                        tokenAndQueue.setToken(cursor.getInt(13));
                        tokenAndQueue.setDisplayToken(cursor.getString(14));
                        tokenAndQueue.setQueueStatus(QueueStatusEnum.valueOf(cursor.getString(15)));
                        tokenAndQueue.setServiceEndTime(cursor.getString(16));
                        tokenAndQueue.setRatingCount(cursor.getInt(17));
                        tokenAndQueue.setAverageServiceTime(cursor.getInt(18));
                        tokenAndQueue.setHoursSaved(cursor.getInt(19));
                        tokenAndQueue.setCreateDate(cursor.getString(20));
                        tokenAndQueue.setBusinessType(cursor.getString(21) == null ? null : BusinessTypeEnum.valueOf(cursor.getString(21)));
                        tokenAndQueue.setGeoHash(cursor.getString(22));
                        tokenAndQueue.setTown(cursor.getString(23));
                        tokenAndQueue.setArea(cursor.getString(24));
                        tokenAndQueue.setDisplayImage(cursor.getString(25));
                        tokenAndQueue.setQueueUserId(cursor.getString(26));
                        tokenAndQueue.setPurchaseOrderState(PurchaseOrderStateEnum.valueOf(cursor.getString(27)));
                        tokenAndQueue.setTransactionId(cursor.getString(28));
                        tokenAndQueue.setTimeSlotMessage(cursor.getString(29));
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

    public static ArrayList<JsonTokenAndQueue> getCurrentQueueObjectList(String codeQR) {
        ArrayList<JsonTokenAndQueue> tokenAndQueueList = new ArrayList<>();
        try {
            Cursor cursor = dbHandler.getReadableDatabase().query(true, TokenQueue.TABLE_NAME, null, TokenQueue.CODE_QR + "=?", new String[]{codeQR}, null, null, null, null);
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
                        tokenAndQueue.setDisplayServingNumber(cursor.getString(11));
                        tokenAndQueue.setLastNumber(cursor.getInt(12));
                        tokenAndQueue.setToken(cursor.getInt(13));
                        tokenAndQueue.setDisplayToken(cursor.getString(14));
                        tokenAndQueue.setQueueStatus(QueueStatusEnum.valueOf(cursor.getString(15)));
                        tokenAndQueue.setServiceEndTime(cursor.getString(16));
                        tokenAndQueue.setRatingCount(cursor.getInt(17));
                        tokenAndQueue.setAverageServiceTime(cursor.getInt(18));
                        tokenAndQueue.setHoursSaved(cursor.getInt(19));
                        tokenAndQueue.setCreateDate(cursor.getString(20));
                        tokenAndQueue.setBusinessType(BusinessTypeEnum.valueOf(cursor.getString(21)));
                        tokenAndQueue.setGeoHash(cursor.getString(22));
                        tokenAndQueue.setTown(cursor.getString(23));
                        tokenAndQueue.setArea(cursor.getString(24));
                        tokenAndQueue.setDisplayImage(cursor.getString(25));
                        tokenAndQueue.setQueueUserId(cursor.getString(26));
                        tokenAndQueue.setPurchaseOrderState(cursor.getString(27) == null ? null : PurchaseOrderStateEnum.valueOf(cursor.getString(27)));
                        tokenAndQueue.setTransactionId(cursor.getString(28));
                        tokenAndQueue.setTimeSlotMessage(cursor.getString(29));
                        tokenAndQueueList.add(tokenAndQueue);
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
        return tokenAndQueueList;
    }

    public static JsonTokenAndQueue getHistoryQueueObject(String codeQR, String token) {
        JsonTokenAndQueue tokenAndQueue = null;
        Cursor cursor = dbHandler.getReadableDatabase().query(true, TokenQueueHistory.TABLE_NAME, null, TokenQueue.CODE_QR + "=?" + " AND " + TokenQueue.TOKEN + " = ?", new String[]{codeQR, token}, null, null, TokenQueue.CREATE_DATE, null);
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
                    tokenAndQueue.setDisplayServingNumber(cursor.getString(11));
                    tokenAndQueue.setLastNumber(cursor.getInt(12));
                    tokenAndQueue.setToken(cursor.getInt(13));
                    tokenAndQueue.setDisplayToken(cursor.getString(14));
                    tokenAndQueue.setQueueStatus(cursor.getString(15) == null ? null : QueueStatusEnum.valueOf(cursor.getString(15)));
                    tokenAndQueue.setServiceEndTime(cursor.getString(16));
                    tokenAndQueue.setRatingCount(cursor.getInt(17));
                    tokenAndQueue.setAverageServiceTime(cursor.getInt(18));
                    tokenAndQueue.setHoursSaved(cursor.getInt(19));
                    tokenAndQueue.setCreateDate(cursor.getString(20));
                    tokenAndQueue.setBusinessType(BusinessTypeEnum.valueOf(cursor.getString(21)));
                    tokenAndQueue.setGeoHash(cursor.getString(22));
                    tokenAndQueue.setTown(cursor.getString(23));
                    tokenAndQueue.setArea(cursor.getString(24));
                    tokenAndQueue.setDisplayImage(cursor.getString(25));
                    tokenAndQueue.setQueueUserId(cursor.getString(26));
                    tokenAndQueue.setPurchaseOrderState(cursor.getString(27) == null ? null : PurchaseOrderStateEnum.valueOf(cursor.getString(27)));
                    tokenAndQueue.setTransactionId(cursor.getString(28));
                    tokenAndQueue.setTimeSlotMessage(cursor.getString(29));
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
                        tokenAndQueue.setDisplayServingNumber(cursor.getString(11));
                        tokenAndQueue.setLastNumber(cursor.getInt(12));
                        tokenAndQueue.setToken(cursor.getInt(13));
                        tokenAndQueue.setDisplayToken(cursor.getString(14));
                        tokenAndQueue.setQueueStatus(cursor.getString(15) == null ? null : QueueStatusEnum.valueOf(cursor.getString(15)));
                        tokenAndQueue.setServiceEndTime(cursor.getString(16));
                        tokenAndQueue.setRatingCount(cursor.getInt(17));
                        tokenAndQueue.setAverageServiceTime(cursor.getInt(18));
                        tokenAndQueue.setHoursSaved(cursor.getInt(19));
                        tokenAndQueue.setCreateDate(cursor.getString(20));
                        tokenAndQueue.setBusinessType(BusinessTypeEnum.valueOf(cursor.getString(21)));
                        tokenAndQueue.setGeoHash(cursor.getString(22));
                        tokenAndQueue.setTown(cursor.getString(23));
                        tokenAndQueue.setArea(cursor.getString(24));
                        tokenAndQueue.setDisplayImage(cursor.getString(25));
                        tokenAndQueue.setQueueUserId(cursor.getString(26));
                        tokenAndQueue.setPurchaseOrderState(cursor.getString(27) == null ? null : PurchaseOrderStateEnum.valueOf(cursor.getString(27)));
                        tokenAndQueue.setTransactionId(cursor.getString(28));
                        tokenAndQueue.setTimeSlotMessage(cursor.getString(29));
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

    public static boolean saveCurrentQueue(List<JsonTokenAndQueue> list) {
        for (JsonTokenAndQueue tokenAndQueue : list) {
            //@TODO hth re check  updating the existing value
            JsonTokenAndQueue jtk = TokenAndQueueDB.getCurrentQueueObject(tokenAndQueue.getCodeQR(), String.valueOf(tokenAndQueue.getToken()));
            if (null != jtk) {
                tokenAndQueue.setServiceEndTime(jtk.getServiceEndTime());
            }
            ContentValues values = createQueueContentValues(tokenAndQueue);
            try {
                long successCount = dbHandler.getWritableDb().insertWithOnConflict(TokenQueue.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                Log.d(TAG, "Data Saved current queue " + successCount);
            } catch (SQLException e) {
                Log.e(TAG, "Error saveCurrentQueue reason=" + e.getLocalizedMessage(), e);
            }
        }
        return true;
    }

    public static void deleteHistoryQueue() {
        dbHandler.getWritableDb().execSQL("delete from " + TokenQueueHistory.TABLE_NAME);
    }

    public static boolean saveHistoryQueue(List<JsonTokenAndQueue> list) {
        for (JsonTokenAndQueue tokenAndQueue : list) {
            ContentValues values = createQueueContentValues(tokenAndQueue);
            try {
                long successCount = dbHandler.getWritableDb().insertWithOnConflict(TokenQueueHistory.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                Log.d(TAG, "Data Saved history queue " + successCount);
            } catch (SQLException e) {
                Log.e(TAG, "Error saveHistoryQueue reason=" + e.getLocalizedMessage(), e);
                FirebaseCrashlytics.getInstance().log("Error saveHistoryQueue reason " + e.getLocalizedMessage());
                FirebaseCrashlytics.getInstance().recordException(e);
            }
        }
        return true;
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
            cv.put(TokenQueue.DISPLAY_SERVING_NUMBER, tokenAndQueue.getDisplayServingNumber());
            cv.put(TokenQueue.LAST_NUMBER, tokenAndQueue.getLastNumber());
            cv.put(TokenQueue.TOKEN, tokenAndQueue.getToken());
            cv.put(TokenQueue.DISPLAY_TOKEN, tokenAndQueue.getDisplayToken());
            if (null != tokenAndQueue.getQueueStatus()) {
                cv.put(TokenQueue.QUEUE_STATUS, tokenAndQueue.getQueueStatus().getName());
            }
            cv.put(TokenQueue.SERVICE_END_TIME, tokenAndQueue.getServiceEndTime());
            cv.put(TokenQueue.RATING_COUNT, tokenAndQueue.getRatingCount());
            cv.put(TokenQueue.AVERAGE_SERVICE_TIME, tokenAndQueue.getAverageServiceTime());
            cv.put(TokenQueue.HOURS_SAVED, tokenAndQueue.getHoursSaved());
            cv.put(TokenQueue.CREATE_DATE, tokenAndQueue.getCreateDate());
            cv.put(TokenQueue.AREA, tokenAndQueue.getArea());
            cv.put(TokenQueue.TOWN, tokenAndQueue.getTown());
            cv.put(TokenQueue.BUSINESS_TYPE, tokenAndQueue.getBusinessType().name());
            cv.put(TokenQueue.GEO_HASH, tokenAndQueue.getGeoHash());
            cv.put(TokenQueue.DISPLAY_IMAGE, tokenAndQueue.getDisplayImage());
            cv.put(TokenQueue.QID, tokenAndQueue.getQueueUserId());
            cv.put(TokenQueue.PURCHASE_ORDER_STATE, tokenAndQueue.getPurchaseOrderState().name());
            cv.put(TokenQueue.TRANSACTION_ID, getTransactionID(tokenAndQueue));
            if (StringUtils.isNotBlank(tokenAndQueue.getTimeSlotMessage())) {
                cv.put(TokenQueue.TIME_SLOT, tokenAndQueue.getTimeSlotMessage());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error createQueueContentValue reason=" + e.getLocalizedMessage(), e);
        }
        return cv;
    }

    public static void saveJoinQueueObject(JsonTokenAndQueue object) {
        ContentValues values = createQueueContentValues(object);
        try {
            long rowInsertCount = dbHandler.getWritableDb().insertWithOnConflict(TokenQueue.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            Log.d(TAG, "Data Saved in current queue " + rowInsertCount);
        } catch (SQLException e) {
            Log.e(TAG, "Error saveJoinQueueObject reason=" + e.getLocalizedMessage(), e);
        }
    }

//    public static void updateJoinQueueObject(String codeQR, String servingno, String token) {
//        try {
//            ContentValues con = new ContentValues();
//            con.put(TokenQueue.SERVING_NUMBER, servingno);
//            con.put(TokenQueue.TOKEN, token);
//            int successCount = dbHandler.getWritableDb().update(TokenQueue.TABLE_NAME, con, TokenQueue.CODE_QR + "=?", new String[]{codeQR});
//            Log.d(TAG, "Data Saved " + TokenQueue.TABLE_NAME + " queue " + String.valueOf(successCount));
//            //AppUtilities.exportDatabase(LaunchActivity.getLaunchActivity());
//        } catch (Exception e) {
//            Log.e(TAG, "Error updateJoinQueueObject reason=" + e.getLocalizedMessage(), e);
//        }
//    }

    public static boolean updateCurrentListQueueObject(String codeQR, String servingNumber, String token) {
        try {
            ContentValues con = new ContentValues();
            con.put(TokenQueue.SERVING_NUMBER, servingNumber);
            //  con.put(TokenQueue.TOKEN, token);
            int successCount = dbHandler.getWritableDb().update(TokenQueue.TABLE_NAME, con, TokenQueue.CODE_QR + "=?" + " AND " + TokenQueue.TOKEN + " = ?", new String[]{codeQR, token});
            Log.d(TAG, "Data Saved " + TokenQueue.TABLE_NAME + " queue " + successCount);

            return successCount > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error updateJoinQueueObject reason=" + e.getLocalizedMessage(), e);
            return false;
        }
    }

    public static boolean updateCurrentListOrderObject(String codeQR, String orderState, String token) {
        try {
            ContentValues con = new ContentValues();
            con.put(TokenQueue.PURCHASE_ORDER_STATE, orderState);
            //  con.put(TokenQueue.TOKEN, token);
            int successCount = dbHandler.getWritableDb().update(TokenQueue.TABLE_NAME, con, TokenQueue.CODE_QR + "=?" + " AND " + TokenQueue.TOKEN + " = ?", new String[]{codeQR, token});
            Log.d(TAG, "Data Saved " + TokenQueue.TABLE_NAME + " queue " + successCount);

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

    private static String getTransactionID(JsonTokenAndQueue jsonTokenAndQueue) {
        if (PurchaseOrderStateEnum.IN == jsonTokenAndQueue.getPurchaseOrderState()) {
            return "";
        } else {
            if (null == jsonTokenAndQueue.getJsonPurchaseOrder()) {
                return "";
            } else {
                return jsonTokenAndQueue.getJsonPurchaseOrder().getTransactionId();
            }
        }
    }

    public static JsonTokenAndQueue findByQRCode(String codeQR) {
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
                    tokenAndQueue.setTokenAvailableFrom(cursor.getInt(6));
                    tokenAndQueue.setStartHour(cursor.getInt(7));
                    tokenAndQueue.setEndHour(cursor.getInt(8));
                    tokenAndQueue.setTopic(cursor.getString(9));
                    tokenAndQueue.setServingNumber(cursor.getInt(10));
                    tokenAndQueue.setDisplayServingNumber(cursor.getString(11));
                    tokenAndQueue.setLastNumber(cursor.getInt(12));
                    tokenAndQueue.setToken(cursor.getInt(13));
                    tokenAndQueue.setDisplayToken(cursor.getString(14));
                    tokenAndQueue.setQueueStatus(cursor.getString(15) == null ? null : QueueStatusEnum.valueOf(cursor.getString(15)));
                    tokenAndQueue.setServiceEndTime(cursor.getString(16));
                    tokenAndQueue.setRatingCount(cursor.getInt(17));
                    tokenAndQueue.setAverageServiceTime(cursor.getInt(18));
                    tokenAndQueue.setHoursSaved(cursor.getInt(19));
                    tokenAndQueue.setCreateDate(cursor.getString(20));
                    tokenAndQueue.setBusinessType(BusinessTypeEnum.valueOf(cursor.getString(21)));
                    tokenAndQueue.setGeoHash(cursor.getString(22));
                    tokenAndQueue.setTown(cursor.getString(23));
                    tokenAndQueue.setArea(cursor.getString(24));
                    tokenAndQueue.setDisplayImage(cursor.getString(25));
                    tokenAndQueue.setQueueUserId(cursor.getString(26));
                    tokenAndQueue.setPurchaseOrderState(cursor.getString(27) == null ? null : PurchaseOrderStateEnum.valueOf(cursor.getString(27)));
                    tokenAndQueue.setTransactionId(cursor.getString(28));
                    tokenAndQueue.setTimeSlotMessage(cursor.getString(29));
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
}

