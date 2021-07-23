package com.noqapp.android.merchant.model.database.utils;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.util.Log;

import com.noqapp.android.merchant.model.database.DatabaseTable;

import java.util.ArrayList;
import java.util.List;

import static com.noqapp.android.merchant.views.activities.AppInitialize.dbHandler;

public class PreferredStoreDB {
    private static final String TAG = PreferredStoreDB.class.getSimpleName();
    public static void insertPreferredStore(String data) {
        try {
            String[] temp = data.split(",");
            Log.d("Record",data);
            ContentValues cv = new ContentValues();
            cv.put(DatabaseTable.PreferredStore.PRODUCT_ID, temp[0]);
            cv.put(DatabaseTable.PreferredStore.BIZ_STORE_ID, temp[1]);
            cv.put(DatabaseTable.PreferredStore.DISPLAY_PRICE, temp[2]);
            cv.put(DatabaseTable.PreferredStore.PRODUCT_NAME, temp[3]);
            cv.put(DatabaseTable.PreferredStore.PRODUCT_INFO, temp[4]);
            cv.put(DatabaseTable.PreferredStore.STORE_CAT_ID, temp[5]);
            cv.put(DatabaseTable.PreferredStore.PRODUCT_TYPE, temp[6]);
            cv.put(DatabaseTable.PreferredStore.PRODUCT_UNIT_VALUE, temp[7]);
            cv.put(DatabaseTable.PreferredStore.PRODUCT_UNIT_MEASURE, temp[8]);
            try {
                long successCount = dbHandler.getWritableDb().insert(
                    DatabaseTable.PreferredStore.TABLE_NAME,
                    null,
                    cv);
                Log.d(TAG, "Data insert PreferredStore " + successCount);
            } catch (SQLException e) {
                Log.e(TAG, "Error insert PreferredStore reason=" + e.getLocalizedMessage(), e);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

//    public static void deleteData(){
//        dbHandler.getWritableDb().execSQL("delete from "+ DatabaseTable.PreferredStore.TABLE_NAME);
//    }

    public static void deletePreferredStore(String bizStoreID) {
        try {
            int out = dbHandler.getWritableDb().delete(
                DatabaseTable.PreferredStore.TABLE_NAME,
                DatabaseTable.PreferredStore.BIZ_STORE_ID + "=?",
                new String[]{bizStoreID});
            Log.v(TAG, "PreferredStore deleted: " + out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<String> getPreferredStoreDataList(String bizStoreID) {
        List<String> dataList = new ArrayList<>();
        Cursor cursor = dbHandler.getWritableDb().query(
            DatabaseTable.PreferredStore.TABLE_NAME,
            null,
            DatabaseTable.PreferredStore.BIZ_STORE_ID + "=?",
            new String[]{bizStoreID}, null, null, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                try {
                    while (cursor.moveToNext()) {
                        dataList.add(cursor.getString(3));
                    }
                } finally {
                    cursor.close();
                }
            }
        }
        return dataList;
    }
    public static List<String> getPreferredStoreDataList(String bizStoreID,String medicineType) {
        List<String> dataList = new ArrayList<>();
        Cursor cursor = dbHandler.getWritableDb().query(
            true,
            DatabaseTable.PreferredStore.TABLE_NAME,
            null,
            DatabaseTable.PreferredStore.BIZ_STORE_ID + "=?" + " AND " + DatabaseTable.PreferredStore.STORE_CAT_ID + " = ?",
            new String[]{bizStoreID, medicineType}, null, null, null, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                try {
                    while (cursor.moveToNext()) {
                        dataList.add(cursor.getString(3));
                    }
                } finally {
                    cursor.close();
                }
            }
        }
        return dataList;
    }

    public static boolean isMedicineExist( String productName) {
        String whereClause = DatabaseTable.PreferredStore.PRODUCT_NAME + " = ?";
        return DatabaseUtils.longForQuery(
            dbHandler.getWritableDb(),
            "SELECT COUNT(*) FROM " + DatabaseTable.PreferredStore.TABLE_NAME + " WHERE " + whereClause,
            new String[]{productName}) > 0;
    }
}
