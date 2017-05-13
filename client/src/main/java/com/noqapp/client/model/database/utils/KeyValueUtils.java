package com.noqapp.client.model.database.utils;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.noqapp.client.model.database.DBUtils;
import com.noqapp.client.model.database.DatabaseTable;

import static com.noqapp.client.views.activities.LaunchActivity.RDH;

/**
 * User: hitender
 * Date: 5/8/17 1:33 PM
 */
public class KeyValueUtils {
    private static final String TAG = KeyValueUtils.class.getSimpleName();

    private KeyValueUtils() {
    }

    public static boolean updateInsert(String key, String value) {
        ContentValues values = new ContentValues();
        values.put(DatabaseTable.KeyValue.KEY, key);
        values.put(DatabaseTable.KeyValue.VALUE, value);

        int update = RDH.getWritableDatabase().update(
                DatabaseTable.KeyValue.TABLE_NAME,
                values,
                DatabaseTable.KeyValue.KEY + "=?",
                new String[]{key}
        );
        boolean updateSuccess = update <= 0;
        if (updateSuccess) {
            long code = RDH.getWritableDatabase().insert(
                    DatabaseTable.KeyValue.TABLE_NAME,
                    null,
                    values
            );

            return code != -1;
        } else {
            return true;
        }
    }

    public static boolean deleteKey(String key) {
        return RDH.getWritableDatabase().delete(
                DatabaseTable.KeyValue.TABLE_NAME,
                DatabaseTable.KeyValue.KEY + "=?",
                new String[]{key}
        ) > 0;
    }

    public static boolean updateValuesForKeyWithBlank(String key) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseTable.KeyValue.VALUE, "");

        return RDH.getWritableDatabase().update(
                DatabaseTable.KeyValue.TABLE_NAME,
                contentValues,
                DatabaseTable.KeyValue.KEY + "=?",
                new String[]{key}
        ) > 0;
    }

    //http://www.androidhive.info/2011/11/android-sqlite-database-tutorial/
    public static String getValue(String key) {
        String value = "";
        Cursor cursor = null;
        try {
            cursor = RDH.getReadableDatabase().query(
                    DatabaseTable.KeyValue.TABLE_NAME,
                    new String[]{DatabaseTable.KeyValue.VALUE},
                    DatabaseTable.KeyValue.KEY + "=?",
                    new String[]{key},
                    null,
                    null,
                    null
            );

            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                value = cursor.getString(cursor.getColumnIndex(DatabaseTable.KeyValue.VALUE));
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting value " + e.getLocalizedMessage(), e);
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }

        return value;
    }

    public static void deleteAll() {
        DBUtils.clearDB(DatabaseTable.KeyValue.TABLE_NAME);
    }

    public static class KEYS {
        public static final String XR_MAIL = "X-R-MAIL";
        public static final String XR_AUTH = "X-R-AUTH";
        public static final String XR_DID = "X-R-DID";

        private KEYS() {
        }
    }

    public static boolean doesTableExists() {
        int count = 0;
        Cursor cursor = null;
        try {
            cursor = RDH.getReadableDatabase().rawQuery(
                    "SELECT count(*) FROM sqlite_master WHERE " +
                            "type = 'table' AND " +
                            "name = '" + DatabaseTable.KeyValue.TABLE_NAME + "'",
                    null);

            if (cursor.moveToNext()) {
                count = cursor.getInt(0);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error counting tables " + e.getLocalizedMessage(), e);
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }

        Log.d(TAG, DatabaseTable.KeyValue.TABLE_NAME + " exists=" + (count > 0));
        return count > 0;
    }
}