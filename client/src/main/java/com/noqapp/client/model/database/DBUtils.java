package com.noqapp.client.model.database;

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.util.Log;

import static com.noqapp.client.views.activities.LaunchActivity.RDH;

/**
 * User: hitender
 * Date: 5/9/17 6:46 PM
 */

public class DBUtils {
    private static final String TAG = DBUtils.class.getSimpleName();

    private DBUtils() {
    }

    /**
     * Delete all tables and re-create all tables with default settings.
     */
    public static void dbReInitialize() {
        dbReInitializeNonKeyValues();
    }

    public static void dbReInitializeNonKeyValues() {
        Log.d(TAG, "Initialize database");

        /** Delete all tables. */
        RDH.getWritableDatabase().execSQL("Drop table if exists " + DatabaseTable.TokenQueue.TABLE_NAME);
        RDH.getWritableDatabase().execSQL("Drop table if exists " + DatabaseTable.TokenQueueHistory.TABLE_NAME);

        /** Create tables. */
        CreateTable.createTableTokenQueue(RDH.getDb());
        CreateTable.createTableTokenQueueHistory(RDH.getDb());
    }

    /**
     * Counts tables in SQLite.
     *
     * @return
     */
    public static int countTables() {
        int count = 0;
        Cursor cursor = null;
        try {
            cursor = RDH.getReadableDatabase().rawQuery(
                    "SELECT count(*) FROM sqlite_master WHERE " +
                            "type = 'table' AND " +
                            "name != 'android_metadata' AND " +
                            "name != 'sqlite_sequence'",
                    null);

            if (cursor.moveToNext()) {
                count = cursor.getInt(0);
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed counting tables as DB does not exists, reason=" + e.getLocalizedMessage());
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }

        Log.d(TAG, "Number of table count in db " + count);
        return count;
    }

    public static void clearDB(String tableName) {
        RDH.getReadableDatabase().delete(
                tableName,
                null,
                null
        );
    }

    /**
     * Escape business name like "St John's Bar & Grill" and replaces string with "'St John''s Bar & Grill'"
     *
     * @param value
     * @return
     */
    public static String sqlEscapeString(String value) {
        return DatabaseUtils.sqlEscapeString(value);
    }
}
