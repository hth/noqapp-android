package com.noqapp.android.merchant.model.database.utils;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.noqapp.android.merchant.model.database.DatabaseTable;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.views.pojos.MedicalFile;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static com.noqapp.android.merchant.views.activities.AppInitialize.dbHandler;

public class MedicalFilesDB {
    private static final String KEY_FORM_SUBMITTED = "1";
    private static final String KEY_FORM_NOT_SUBMITTED = "0";
    private static final String TAG = MedicalFilesDB.class.getSimpleName();

    public static void insertMedicalFile(String recordReferenceId, String fileLocation) {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseTable.MedicalFiles.RECORD_REFERENCE_ID, recordReferenceId);
        cv.put(DatabaseTable.MedicalFiles.UPLOAD_STATUS, "N"); //BooleanReplacementEnum
        cv.put(DatabaseTable.MedicalFiles.FILE_LOCATION, fileLocation);
        cv.put(DatabaseTable.MedicalFiles.FORM_SUBMISSION_STATUS, KEY_FORM_NOT_SUBMITTED);
        cv.put(DatabaseTable.MedicalFiles.UPLOAD_ATTEMPT_COUNT, "0");
        DateFormat dateFormat = new SimpleDateFormat(Constants.ISO8601_FMT, Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        String dateString = dateFormat.format(new Date());
        cv.put(DatabaseTable.MedicalFiles.FILE_CREATED_DATE, dateString);
        try {
            long successCount = dbHandler.getWritableDb().insertWithOnConflict(DatabaseTable.MedicalFiles.TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
            Log.d(TAG, "Data insert medicalFiles " + successCount + " file location " + fileLocation);
        } catch (SQLException e) {
            Log.e(TAG, "Error insert medicalFiles reason=" + e.getLocalizedMessage(), e);
        }
    }

    public static List<MedicalFile> getMedicalFileList() {
        String query = "SELECT *  FROM " + DatabaseTable.MedicalFiles.TABLE_NAME;
        List<MedicalFile> medicalFiles = new ArrayList<>();
        Cursor cursor = dbHandler.getWritableDb().rawQuery(query, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                try {
                    while (cursor.moveToNext()) {
                        MedicalFile medicalFile = new MedicalFile();
                        medicalFile.setRecordReferenceId(cursor.getString(0));
                        medicalFile.setFileLocation(cursor.getString(1));
                        medicalFile.setFileCreatedDate(cursor.getString(2));
                        medicalFile.setUploadStatus(cursor.getString(3));
                        medicalFile.setUploadAttemptCount(cursor.getString(4));
                        medicalFile.setFormSubmissionStatus(cursor.getString(5));
                        medicalFiles.add(medicalFile);
                    }
                } finally {
                    cursor.close();
                }
            }
        }
        return medicalFiles;
    }

    public static void deleteMedicalFile(String recordReferenceId) {
        try {
            int out = dbHandler.getWritableDb().delete(
                DatabaseTable.MedicalFiles.TABLE_NAME,
                DatabaseTable.MedicalFiles.RECORD_REFERENCE_ID + "=?",
                new String[]{recordReferenceId});
            Log.v(TAG, "medical record deleted: " + out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateMedicalFile(MedicalFile medicalFile) {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseTable.MedicalFiles.UPLOAD_STATUS, "S");
        cv.put(DatabaseTable.MedicalFiles.UPLOAD_ATTEMPT_COUNT, String.valueOf(Integer.parseInt(medicalFile.getUploadAttemptCount()) + 1));
        try {
            long successCount = dbHandler.getWritableDb().update(
                DatabaseTable.MedicalFiles.TABLE_NAME,
                cv,
                DatabaseTable.MedicalFiles.RECORD_REFERENCE_ID + "=" + medicalFile.getRecordReferenceId(),
                null);
            Log.d(TAG, "Data updated notification " + successCount);
        } catch (SQLException e) {
            Log.e(TAG, "Error update notification reason=" + e.getLocalizedMessage(), e);
        }
    }
}
