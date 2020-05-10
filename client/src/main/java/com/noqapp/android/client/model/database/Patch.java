package com.noqapp.android.client.model.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * User: hitender
 * Date: 5/9/17 6:32 PM
 */
public class Patch {
    private static final String TAG = Patch.class.getSimpleName();

    Patch(int migrateFrom, int migrateTo, String buildNumber) {
        Log.d(TAG, "DB Migrate from " + migrateFrom + " to " + migrateTo + ". Since build " + buildNumber);
    }

    public void apply(SQLiteDatabase db) {
    }
}
