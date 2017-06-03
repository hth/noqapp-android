package com.noqapp.android.client.model.database.utils;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.noqapp.android.client.model.database.DatabaseTable;

import static com.noqapp.android.client.views.activities.LaunchActivity.dbHandler;

/**
 * User: hitender
 * Date: 5/28/17 2:28 PM
 */
public class ReviewDB {
    public static final String KEY_REVEIW = "KY_RE";
    public static final String KEY_SKIP = "KY_SK";
    public static final String KEY_GOTO = "KY_GT";
    private static final String TAG = ReviewDB.class.getSimpleName();

    public static void insert(String key, String codeQR, String value) {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseTable.Review.KEY, key);
        cv.put(DatabaseTable.Review.CODE_QR, codeQR);
        cv.put(DatabaseTable.Review.VALUE, value);

        try {
            long successCount = dbHandler.getWritableDb().insertWithOnConflict(
                    DatabaseTable.Review.TABLE_NAME,
                    null,
                    cv,
                    SQLiteDatabase.CONFLICT_REPLACE);

            Log.d(TAG, "Data insert review " + String.valueOf(successCount));
        } catch (SQLException e) {
            Log.e(TAG, "Error insert review reason=" + e.getLocalizedMessage(), e);
        }
    }

    public static String getValue(String key, String codeQR) {
        Cursor cursor = dbHandler.getReadableDatabase().query(true, DatabaseTable.Review.TABLE_NAME, null,
                DatabaseTable.Review.KEY + "=? and " + DatabaseTable.Review.CODE_QR + "=?", new String[]{key, codeQR}, null, null, null, null);

        String value = null;
        if (cursor != null) {
            if (cursor.getCount() > 0) {

                try {
                    cursor.moveToNext();
                    value = cursor.getString(2);
                } finally {
                    cursor.close();
                }
            }
        }

        return value;
    }

    public static String getValue(String key) {
        Cursor cursor = dbHandler.getReadableDatabase().query(true, DatabaseTable.Review.TABLE_NAME, null, DatabaseTable.Review.KEY + "=?", new String[]{key}, null, null, null, null);
        String value = null;
        if (cursor != null) {
            if (cursor.getCount() > 0) {

                try {
                    cursor.moveToNext();
                    value = cursor.getString(1);
                } finally {
                    cursor.close();
                }
            }
        }

        return value;
    }
}
