package com.noqapp.android.client.model.database.utils;

import static com.noqapp.android.client.views.activities.LaunchActivity.dbHandler;

import com.noqapp.android.client.model.database.DatabaseTable;
import com.noqapp.android.client.presenter.beans.ReviewData;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * User: hitender
 * Date: 5/28/17 2:28 PM
 */
public class ReviewDB {
    public static final String KEY_REVIEW = "KEY_REVIEW";
    public static final String KEY_SKIP = "KEY_SKIP";
    public static final String KEY_GOTO = "KEY_GOTO";

    private static final String TAG = ReviewDB.class.getSimpleName();

    public static void insert(String key, String codeQR, String token, String value, String quserID) {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseTable.Review.KEY, key);
        cv.put(DatabaseTable.Review.CODE_QR, codeQR);
        cv.put(DatabaseTable.Review.TOKEN, token);
        cv.put(DatabaseTable.Review.VALUE, value);
        cv.put(DatabaseTable.Review.Q_USER_ID,quserID);
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

    public static String getValue(String key, String codeQR,String token) {
        try {
            Cursor cursor = dbHandler.getReadableDatabase().query(false, DatabaseTable.Review.TABLE_NAME, null,
                    DatabaseTable.Review.KEY + "=? and " + DatabaseTable.Review.CODE_QR + "=? and " + DatabaseTable.Review.TOKEN + "=?", new String[]{key, codeQR, token}, null, null, null, null);
            String value = "";
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
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }

    public static ReviewData getValue(String key) {
        ReviewData reviewData = new ReviewData();
        Cursor cursor = dbHandler.getReadableDatabase().query(true, DatabaseTable.Review.TABLE_NAME, null, DatabaseTable.Review.KEY + "=?", new String[]{key}, null, null, null, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {

                try {
                    cursor.moveToNext();
                    reviewData.setKey(cursor.getString(0));
                    reviewData.setCodeQR(cursor.getString(1));
                    reviewData.setValue(cursor.getString(2));
                    reviewData.setToken(cursor.getString(3));
                    reviewData.setqUserId(cursor.getString(4));
                } finally {
                    cursor.close();
                }
            }
        }

        return reviewData;
    }


    public static void deleteReview(String key,String codeQR, String token) {
        try {
            int out = dbHandler.getWritableDb().delete(DatabaseTable.Review.TABLE_NAME,
                    DatabaseTable.Review.KEY + "=? AND " + DatabaseTable.Review.CODE_QR + "=? AND " +
                            DatabaseTable.Review.TOKEN + "=?",
                    new String[]{key, codeQR, token});
            Log.v("review deleted:",""+out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
