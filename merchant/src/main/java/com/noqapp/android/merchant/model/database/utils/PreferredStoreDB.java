package com.noqapp.android.merchant.model.database.utils;

import static com.noqapp.android.merchant.views.activities.BaseLaunchActivity.dbHandler;

import com.noqapp.android.merchant.model.database.DatabaseTable;

import android.database.Cursor;
import android.database.SQLException;
import android.content.ContentValues;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

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
            cv.put(DatabaseTable.PreferredStore.PRODUCT_UNIT_MESAURE, temp[8]);
            try {
                long successCount = dbHandler.getWritableDb().insert(
                        DatabaseTable.PreferredStore.TABLE_NAME,
                        null,
                        cv);
                Log.d(TAG, "Data insert PreferredStore " + String.valueOf(successCount));
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

    public static void deletePreferredStore(String productID) {
        try {
            int out = dbHandler.getWritableDb().delete(DatabaseTable.PreferredStore.TABLE_NAME,
                            DatabaseTable.PreferredStore.PRODUCT_ID + "=?",
                    new String[]{productID});
            Log.v("PreferredStore deleted:", "" + out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<String> getPreferredStoreDataList(String bizStoreID) {
        List<String> dataList = new ArrayList<>();
        Cursor cursor = dbHandler.getWritableDb().query(DatabaseTable.PreferredStore.TABLE_NAME, null,DatabaseTable.PreferredStore.BIZ_STORE_ID + "=?",
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
}
