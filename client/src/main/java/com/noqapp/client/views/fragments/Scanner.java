package com.noqapp.client.views.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.zxing.client.android.CaptureActivity;
import com.noqapp.client.views.activities.BarcodeScannerActivity;

import org.apache.commons.lang3.StringUtils;


public abstract class Scanner extends NoQueueBaseFragment implements CaptureActivity.BarcodeScannedResultCallback {

    private final String TAG = Scanner.class.getSimpleName();
    private final int CAMERA_AND_STORAGE_PERMISSION_CODE = 102;
    private final String[] CAMERA_AND_STORAGE_PERMISSION_PERMS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };



    public Scanner() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        BarcodeScannerActivity.barcodeScannedResultCallback = this;
        return super.onCreateView( inflater,  container,
                 savedInstanceState);
    }






    protected void startScanningBarcode() {
        if (isCameraAndStoragePermissionAllowed()) {
            scanBarcode();
        } else {
            requestCameraAndStoragePermission();
        }
    }

    @Override
    public void barcodeScannedResult(String rawData) {
        Log.v("Scanned CodeQR=", rawData);
        if (StringUtils.isBlank(rawData)) {
            Log.d("MainActivity", "Cancelled scan");
            Toast.makeText(getActivity(), "Cancelled", Toast.LENGTH_LONG).show();
        } else {
            if (rawData.startsWith("https://tp.receiptofi.com")) {
                try{
                    String[] codeQR = rawData.split("/");
                    barcodeResult(codeQR[3]);
                }catch (Exception e){
                    e.printStackTrace();
                }


            } else {
                Toast toast = Toast.makeText(getActivity(), "QR Code is not a NoQueue Code", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }
protected abstract void barcodeResult(String codeqr);
    @Override
    public void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
    }

    private void scanBarcode() {
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        display.getMetrics(dm);
        int width = dm.widthPixels * 2 / 3;
        int height = dm.heightPixels * 1 / 2;
        Intent intent = new Intent(getActivity(),
                BarcodeScannerActivity.class);
        intent.setAction("com.google.zxing.client.android.SCAN");
        intent.putExtra("SCAN_WIDTH", width);
        intent.putExtra("SCAN_HEIGHT", height);
        startActivityForResult(intent, 0);
    }

    private boolean isExternalStoragePermissionAllowed() {
        //Getting the permission status
        int result_read = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);
        int result_write = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        //If permission is granted returning true
        if (result_read == PackageManager.PERMISSION_GRANTED && result_write == PackageManager.PERMISSION_GRANTED)
            return true;
        //If permission is not granted returning false
        return false;
    }

    private boolean isCameraPermissionAllowed() {
        //Getting the permission status
        int result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA);
        //If permission is granted returning true
        if (result == PackageManager.PERMISSION_GRANTED)
            return true;
        //If permission is not granted returning false
        return false;
    }

    private boolean isCameraAndStoragePermissionAllowed() {
        return (isCameraPermissionAllowed() && isExternalStoragePermissionAllowed());
    }


    private void requestCameraAndStoragePermission() {
        ActivityCompat.requestPermissions(getActivity(), CAMERA_AND_STORAGE_PERMISSION_PERMS,
                CAMERA_AND_STORAGE_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == CAMERA_AND_STORAGE_PERMISSION_CODE) {
            //both remaining permission allowed
            if (grantResults.length == 2 && (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                scanBarcode();
            } else if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {//one remaining permission allowed
                scanBarcode();
            } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                //No permission allowed
                //Do nothing
            }
        }
    }
}
