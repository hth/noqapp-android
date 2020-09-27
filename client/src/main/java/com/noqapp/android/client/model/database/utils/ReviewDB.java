package com.noqapp.android.client.model.database.utils;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;

import com.noqapp.android.client.model.database.DatabaseTable;
import com.noqapp.android.client.presenter.beans.ReviewData;

import static com.noqapp.android.client.views.activities.MyApplication.dbHandler;

/**
 * User: hitender
 * Date: 5/28/17 2:28 PM
 */
public class ReviewDB {

    private static final String TAG = ReviewDB.class.getSimpleName();

    public static void insert(ContentValues value) {
        try {
            long successCount = dbHandler.getWritableDb().insert(
                    DatabaseTable.Review.TABLE_NAME,
                    null,
                    value);
            Log.d(TAG, "Data insert queue review" + String.valueOf(successCount));
        } catch (SQLException e) {
            Log.e(TAG, "Error insert queue review reason=" + e.getLocalizedMessage(), e);
        }
    }

    public static ReviewData getValue(String codeQR, String token) {
        try {
            final Cursor cursor = dbHandler.getWritableDatabase().rawQuery("SELECT * FROM " + DatabaseTable.Review.TABLE_NAME + " where "
                    + DatabaseTable.Review.CODE_QR + " = '" + codeQR + "' and " + DatabaseTable.Review.TOKEN + " = '" + token + "'", null);
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    try {
                        cursor.moveToNext();
                        ReviewData reviewData = new ReviewData();
                        reviewData.setCodeQR(cursor.getString(0));
                        reviewData.setToken(cursor.getString(1));
                        reviewData.setqUserId(cursor.getString(2));
                        reviewData.setIsReviewShown(cursor.getString(3));
                        reviewData.setIsSkipped(cursor.getString(4));
                        reviewData.setGotoCounter(cursor.getString(5));
                        reviewData.setIsBuzzerShow(cursor.getString(6));
                        return reviewData;
                    } finally {
                        cursor.close();
                    }
                }
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static ReviewData getPendingReview() {
        ReviewData reviewData = new ReviewData();
        Cursor cursor = dbHandler.getReadableDatabase().query(true, DatabaseTable.Review.TABLE_NAME, null, DatabaseTable.Review.KEY_REVIEW_SHOWN + "=?", new String[]{"1"}, null, null, null, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                try {
                    cursor.moveToNext();
                    reviewData.setCodeQR(cursor.getString(0));
                    reviewData.setToken(cursor.getString(1));
                    reviewData.setqUserId(cursor.getString(2));
                    reviewData.setIsReviewShown(cursor.getString(3));
                    reviewData.setIsSkipped(cursor.getString(4));
                    reviewData.setGotoCounter(cursor.getString(5));
                    reviewData.setIsBuzzerShow(cursor.getString(6));
                } finally {
                    cursor.close();
                }
            }
        }
        return reviewData;
    }

    public static ReviewData getSkippedQueue() {
        ReviewData reviewData = new ReviewData();
        Cursor cursor = dbHandler.getReadableDatabase().query(true, DatabaseTable.Review.TABLE_NAME, null, DatabaseTable.Review.KEY_SKIP + "=?", new String[]{"1"}, null, null, null, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                try {
                    cursor.moveToNext();
                    reviewData.setCodeQR(cursor.getString(0));
                    reviewData.setToken(cursor.getString(1));
                    reviewData.setqUserId(cursor.getString(2));
                    reviewData.setIsReviewShown(cursor.getString(3));
                    reviewData.setIsSkipped(cursor.getString(4));
                    reviewData.setGotoCounter(cursor.getString(5));
                    reviewData.setIsBuzzerShow(cursor.getString(6));
                } finally {
                    cursor.close();
                }
            }
        }
        return reviewData;
    }


    public static void deleteReview(String codeQR, String token) {
        try {
            int out = dbHandler.getWritableDb().delete(DatabaseTable.Review.TABLE_NAME,
                    DatabaseTable.Review.CODE_QR + "=? AND " +
                            DatabaseTable.Review.TOKEN + "=?",
                    new String[]{codeQR, token});
            Log.v("queue review deleted:", "" + out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void updateReviewRecord(String codeQR, String token, ContentValues cv) {

        try {
            long successCount = dbHandler.getWritableDb().
                    update(DatabaseTable.Review.TABLE_NAME, cv, DatabaseTable.Review.CODE_QR + "=? AND " +
                            DatabaseTable.Review.TOKEN + "=?", new String[]{codeQR, token});
            Log.d(TAG, "ReviewRecord updated " + String.valueOf(successCount));
        } catch (SQLException e) {
            Log.e(TAG, "Error update ReviewRecord reason=" + e.getLocalizedMessage(), e);
        }
    }

    public static void clearReviewTable(){
        try {
            dbHandler.getWritableDb().delete(DatabaseTable.Review.TABLE_NAME, null, null);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
