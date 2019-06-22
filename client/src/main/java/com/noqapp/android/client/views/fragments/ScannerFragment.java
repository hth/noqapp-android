package com.noqapp.android.client.views.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.noqapp.android.client.R;
import com.noqapp.android.common.utils.PermissionUtils;
import com.noqapp.android.client.views.activities.BarcodeCaptureActivity;

import org.apache.commons.lang3.StringUtils;

public abstract class ScannerFragment extends NoQueueBaseFragment {
    private static final int RC_BARCODE_CAPTURE = 23;
    private final String TAG = ScannerFragment.class.getSimpleName();

    public ScannerFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    protected void startScanningBarcode() {
        if (PermissionUtils.isCameraAndStoragePermissionAllowed(getActivity())) {
            scanBarcode();
        } else {
            requestCameraAndStoragePermission();
        }
    }

    protected abstract void barcodeResult(String codeQR, boolean isCategoryData);

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
    }

    private void scanBarcode() {
        Intent intent = new Intent(getActivity(), BarcodeCaptureActivity.class);
        intent.putExtra(BarcodeCaptureActivity.AutoFocus, true);
        intent.putExtra(BarcodeCaptureActivity.UseFlash, false);
        startActivityForResult(intent, RC_BARCODE_CAPTURE);
    }


    private void requestCameraAndStoragePermission() {
        requestPermissions(
                PermissionUtils.CAMERA_AND_STORAGE_PERMISSIONS,
                PermissionUtils.PERMISSION_REQUEST_CAMERA_AND_STORAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PermissionUtils.PERMISSION_REQUEST_CAMERA_AND_STORAGE) {
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
