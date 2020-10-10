package com.noqapp.android.common.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionUtils {

    public static final String LOCATION_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION;
    public static final String[] STORAGE_PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    public static final String[] CAMERA_AND_STORAGE_PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    public static final int PERMISSION_REQUEST_LOCATION = 99;
    public static final int PERMISSION_REQUEST_STORAGE = 100;
    public static final int PERMISSION_REQUEST_CAMERA_AND_STORAGE = 101;

    public static boolean isExternalStoragePermissionAllowed(Context ctx) {
        //Getting the permission status
        int result_read = ContextCompat.checkSelfPermission(ctx, Manifest.permission.READ_EXTERNAL_STORAGE);
        int result_write = ContextCompat.checkSelfPermission(ctx, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        //If permission is granted returning true
        if (result_read == PackageManager.PERMISSION_GRANTED && result_write == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        //If permission is not granted returning false
        return false;
    }

    public static boolean isCameraPermissionAllowed(Context ctx) {
        //Getting the permission status
        int result = ContextCompat.checkSelfPermission(ctx, Manifest.permission.CAMERA);
        //If permission is granted returning true
        if (result == PackageManager.PERMISSION_GRANTED)
            return true;
        //If permission is not granted returning false
        return false;
    }

    public static void requestStoragePermission(Activity activity) {
        ActivityCompat.requestPermissions(
                activity,
                STORAGE_PERMISSIONS,
                PERMISSION_REQUEST_STORAGE);
    }

    public static boolean isCameraAndStoragePermissionAllowed(Context ctx) {
        return (isCameraPermissionAllowed(ctx) && isExternalStoragePermissionAllowed(ctx));
    }
}
