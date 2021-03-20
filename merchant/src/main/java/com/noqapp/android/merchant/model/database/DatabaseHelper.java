package com.noqapp.android.merchant.model.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * User: hitender
 * Date: 5/9/17 6:28 PM
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "noqueuemerchant.db";
    private static final int DB_VERSION = 3;
    private static DatabaseHelper dbInstance;
    private final String TAG = DatabaseHelper.class.getSimpleName();
    private SQLiteDatabase db = null;

    private DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static DatabaseHelper getsInstance(Context context) {
        if (dbInstance == null) {
            dbInstance = new DatabaseHelper(context);
        }
        return dbInstance;
    }

    /**
     * Do not initialize db here.
     *
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "DatabaseHandler onCreate");
        if (null != db) {
            this.db = db;
        }
        CreateTable.createAllTable(db);
    }

    public SQLiteDatabase getWritableDb() {
        if (null == db) {
            Log.d(TAG, "db is NULL, re-initialized");
            db = dbInstance.getWritableDatabase();
        }
        return db;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "DatabaseHandler onUpgrade");
        for (Patch patch : PATCHES) {
            patch.apply(db);
        }
    }

    private static final Patch[] PATCHES = new Patch[]{
        new Patch(1, 2, "1.2.21") {
            public void apply(SQLiteDatabase db) {
                CreateTable.createTablePreferredStore(db);
            }
        }, new Patch(2, 3, "1.2.3") {
            public void apply(SQLiteDatabase db) {
            CreateTable.createTableMedicalFiles(db);
        }
        }
    };

}
