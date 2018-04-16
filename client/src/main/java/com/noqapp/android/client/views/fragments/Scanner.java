package com.noqapp.android.client.views.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.noqapp.android.client.R;
import com.noqapp.android.client.views.activities.BarcodeCaptureActivity;

import org.apache.commons.lang3.StringUtils;

public abstract class Scanner extends NoQueueBaseFragment {
    private final String TAG = Scanner.class.getSimpleName();
    private final int CAMERA_AND_STORAGE_PERMISSION_CODE = 102;
    private final String[] CAMERA_AND_STORAGE_PERMISSION_PERMS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };
    private static final int RC_BARCODE_CAPTURE = 9001;

    public Scanner() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    protected void startScanningBarcode() {
        if (isCameraAndStoragePermissionAllowed()) {
            scanBarcode();
        } else {
            requestCameraAndStoragePermission();
        }
    }

//    @Override
//    public void barcodeScannedResult(String rawData) {
//        Log.v(TAG, "Scanned CodeQR=" + rawData);
//        if (StringUtils.isBlank(rawData)) {
//            Log.d("MainActivity", "Cancelled scan");
//            Toast.makeText(getActivity(), "Cancelled", Toast.LENGTH_LONG).show();
//        } else {
//            if (rawData.startsWith("https://q.noqapp.com")) {
//                try {
//                    String[] codeQR = rawData.split("/");
//                    //endswith - q.htm or b.htm
//                    // to define weather we need to show category screen or join screen
//                    boolean isCategoryData = rawData.endsWith("b.htm");
//                    barcodeResult(codeQR[3], isCategoryData);
//
//                    Answers.getInstance().logCustom(new CustomEvent("Scan")
//                            .putCustomAttribute("codeQR", codeQR[3]));
//                } catch (Exception e) {
//                    Log.e(TAG, "Failed parsing codeQR reason=" + e.getLocalizedMessage(), e);
//                }
//            } else {
//                Toast toast = Toast.makeText(getActivity(), getString(R.string.error_qrcode_scan), Toast.LENGTH_SHORT);
//                toast.show();
//            }
//        }
//    }

    protected abstract void barcodeResult(String codeQR, boolean isCategoryData);

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
    }

    private void scanBarcode() {
//        Display display = getActivity().getWindowManager().getDefaultDisplay();
//        DisplayMetrics dm = new DisplayMetrics();
//        display.getMetrics(dm);
//        int width = dm.widthPixels * 2 / 3;
//        int height = dm.heightPixels / 2;
//        Intent intent = new Intent(getActivity(), BarcodeScannerActivity.class);
//        intent.setAction("com.google.zxing.client.android.SCAN");
//        intent.putExtra("SCAN_WIDTH", width);
//        intent.putExtra("SCAN_HEIGHT", height);
//        startActivityForResult(intent, 0);

        Intent intent = new Intent(getActivity(), BarcodeCaptureActivity.class);
        intent.putExtra(BarcodeCaptureActivity.AutoFocus, true);
        intent.putExtra(BarcodeCaptureActivity.UseFlash, false);

        startActivityForResult(intent, RC_BARCODE_CAPTURE);
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
        ActivityCompat.requestPermissions(
                getActivity(),
                CAMERA_AND_STORAGE_PERMISSION_PERMS,
                CAMERA_AND_STORAGE_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == CAMERA_AND_STORAGE_PERMISSION_CODE) {
            try {
                //both remaining permission allowed
                if (grantResults.length == 2 && (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                    scanBarcode();
                } else if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {//one remaining permission allowed
                    scanBarcode();
                } else if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    //No permission allowed
                    //Do nothing
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {

                    String contents = data.getStringExtra("SCAN_RESULT");
                    String format = data.getStringExtra("SCAN_RESULT_FORMAT");

                    Log.v(TAG, "Scanned CodeQR=" + contents);
                    if (StringUtils.isBlank(contents)) {
                        Log.d("MainActivity", "Cancelled scan");
                        Toast.makeText(getActivity(), "Cancelled", Toast.LENGTH_LONG).show();
                    } else {
                        if (contents.startsWith("https://q.noqapp.com")) {
                            try {
                                String[] codeQR = contents.split("/");
                                //endswith - q.htm or b.htm
                                // to define weather we need to show category screen or join screen
                                boolean isCategoryData = contents.endsWith("b.htm");
                                barcodeResult(codeQR[3], isCategoryData);

                                Answers.getInstance().logCustom(new CustomEvent("Scan")
                                        .putCustomAttribute("codeQR", codeQR[3]));
                            } catch (Exception e) {
                                Log.e(TAG, "Failed parsing codeQR reason=" + e.getLocalizedMessage(), e);
                            }
                        } else {
                            Toast toast = Toast.makeText(getActivity(), getString(R.string.error_qrcode_scan), Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                    Log.d(TAG, "Barcode read: " + contents);
                } else {
                    // statusMessage.setText(R.string.barcode_failure);
                    Log.d(TAG, "No barcode captured, intent data is null");
                }
            } else {

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
