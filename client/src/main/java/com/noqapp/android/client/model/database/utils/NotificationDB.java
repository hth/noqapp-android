package com.noqapp.android.client.model.database.utils;

import static com.noqapp.android.client.views.activities.AppInitialize.dbHandler;

import com.noqapp.android.client.model.database.DatabaseTable;
import com.noqapp.android.common.model.types.BusinessTypeEnum;
import com.noqapp.android.common.pojos.DisplayNotification;
import com.noqapp.android.common.utils.CommonHelper;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by chandra on 8/7/17.
 */
public class NotificationDB {

    public static final String KEY_READ = "1";
    public static final String KEY_UNREAD = "0";
    public static final String KEY_NOTIFY = "KY_NOTI";
    private static final String TAG = NotificationDB.class.getSimpleName();

    public static void insertNotification(String key, String codeQR, String value, String title, String businessType, String imageUrl) {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseTable.Notification.KEY, key);
        cv.put(DatabaseTable.Notification.CODE_QR, codeQR);
        cv.put(DatabaseTable.Notification.BODY, value);
        cv.put(DatabaseTable.Notification.TITLE, title);
        cv.put(DatabaseTable.Notification.STATUS, KEY_UNREAD); // added default
        // Returns the current date with the same format as Javascript's new Date().toJSON(), ISO 8601
        String dateString = CommonHelper.changeUTCDateToString(new Date());
        cv.put(DatabaseTable.Notification.CREATE_DATE, dateString);
        cv.put(DatabaseTable.Notification.BUSINESS_TYPE, businessType);
        cv.put(DatabaseTable.Notification.IMAGE_URL, TextUtils.isEmpty(imageUrl) ? "" : imageUrl);
        try {
            long successCount = dbHandler.getWritableDb().insertWithOnConflict(
                    DatabaseTable.Notification.TABLE_NAME,
                    null,
                    cv,
                    SQLiteDatabase.CONFLICT_REPLACE);

            Log.d(TAG, "Data insert notification " + String.valueOf(successCount));
        } catch (SQLException e) {
            Log.e(TAG, "Error insert notification reason=" + e.getLocalizedMessage(), e);
        }
    }


    public static List<DisplayNotification> getNotificationsList() {

        String query = "SELECT *  FROM " + DatabaseTable.Notification.TABLE_NAME + " ORDER BY " + DatabaseTable.Notification.SEQUENCE + " DESC ";
        List<DisplayNotification> displayNotificationList = new ArrayList<>();
        Cursor cursor = dbHandler.getWritableDb().rawQuery(query, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                try {
                    while (cursor.moveToNext()) {
                        DisplayNotification notificationBeans = new DisplayNotification();
                        notificationBeans.setKey(cursor.getString(0));
                        notificationBeans.setCodeQR(cursor.getString(1));
                        notificationBeans.setMsg(cursor.getString(2));
                        notificationBeans.setTitle(cursor.getString(3));
                        notificationBeans.setStatus(cursor.getString(4));
                        notificationBeans.setSequence(cursor.getInt(5));
                        notificationBeans.setNotificationCreate(cursor.getString(6));
                        notificationBeans.setBusinessType(BusinessTypeEnum.valueOf(cursor.getString(7)));
                        notificationBeans.setImageUrl(cursor.getString(8));
                        displayNotificationList.add(notificationBeans);
                    }
                } finally {
                    cursor.close();
                }
            }
        }
        return displayNotificationList;
    }


    public static void updateNotification() {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseTable.Notification.STATUS, KEY_READ);
        try {
            long successCount = dbHandler.getWritableDb().
                    update(DatabaseTable.Notification.TABLE_NAME, cv, DatabaseTable.Notification.STATUS + "=" + KEY_UNREAD, null);
            Log.d(TAG, "Data updated notification " + String.valueOf(successCount));
        } catch (SQLException e) {
            Log.e(TAG, "Error update notification reason=" + e.getLocalizedMessage(), e);
        }
    }

    public static int getNotificationCount() {
        try {
            String selection = DatabaseTable.Notification.STATUS + " = ? ";
            String[] selectionArgs = {KEY_UNREAD};
            Cursor c = dbHandler.getWritableDb().query(DatabaseTable.Notification.TABLE_NAME, null, selection, selectionArgs, null, null, null);
            int result = c.getCount();
            c.close();
            return result;
        } catch (Exception e) {
            return 0;
        }
    }

    public static void clearNotificationTable() {
        try {
            dbHandler.getWritableDb().delete(DatabaseTable.Notification.TABLE_NAME, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteNotification(Integer sequence, String key) {
        try {
            int out = dbHandler.getWritableDb().delete(DatabaseTable.Notification.TABLE_NAME,
                    DatabaseTable.Notification.SEQUENCE + "=? AND " +
                            DatabaseTable.Notification.KEY + "=?",
                    new String[]{Integer.toString(sequence), key});
            Log.v("notification deleted:", "" + out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
