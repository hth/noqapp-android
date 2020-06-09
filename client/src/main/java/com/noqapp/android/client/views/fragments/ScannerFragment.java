package com.noqapp.android.client.views.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.noqapp.android.client.R;
import com.noqapp.android.client.utils.AnalyticsEvents;
import com.noqapp.android.client.views.activities.BarcodeCaptureActivity;
import com.noqapp.android.client.views.activities.LaunchActivity;
import com.noqapp.android.common.model.types.MessageOriginEnum;
import com.noqapp.android.common.utils.PermissionUtils;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class ScannerFragment extends BaseFragment {
    public static final int RC_BARCODE_CAPTURE = 23;
    private final String TAG = ScannerFragment.class.getSimpleName();
    protected RelativeLayout rl_scan;
    private int requestCode;
    private ScanResult scanResult;

    public ScannerFragment(ScanResult scanResult, int requestCode) {
        this.scanResult = scanResult;
        this.requestCode = requestCode;
    }

    public interface ScanResult {

        void barcodeResult(String codeQR, boolean isCategoryData);

        void qrCodeResult(String[] scanData);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_scanner, container, false);
        rl_scan = view.findViewById(R.id.rl_scan);
        rl_scan.setOnClickListener(v -> {
            startScanningBarcode();
        });
        return view;
    }

    protected void startScanningBarcode() {
        if (PermissionUtils.isCameraAndStoragePermissionAllowed(getActivity())) {
            scanBarcode();
        } else {
            requestCameraAndStoragePermission();
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
    }

    private void scanBarcode() {
        Intent intent = new Intent(getActivity(), BarcodeCaptureActivity.class);
        intent.putExtra(BarcodeCaptureActivity.AutoFocus, true);
        intent.putExtra(BarcodeCaptureActivity.UseFlash, false);
        startActivityForResult(intent, requestCode);
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
        if (resultCode == Activity.RESULT_OK) {
            if (data != null) {
                String contents = data.getStringExtra("SCAN_RESULT");
                String format = data.getStringExtra("SCAN_RESULT_FORMAT");

                Log.v(TAG, "Scanned CodeQR=" + contents);
                if (StringUtils.isBlank(contents)) {
                    Log.d("MainActivity", "Cancelled scan");
                    Toast.makeText(getActivity(), "Cancelled", Toast.LENGTH_LONG).show();
                } else {
                    if (requestCode == RC_BARCODE_CAPTURE) {
                        if (contents.startsWith("https://q.noqapp.com")) {
                            try {
                                if (contents.endsWith(MessageOriginEnum.AU.name())) {
                                    if (null != scanResult) {
                                        String[] codeQR = contents.split("https://q.noqapp.com/");
                                        String[] scanData = codeQR[1].split("#");
                                        scanResult.qrCodeResult(scanData);
                                        Log.d("SCAN RESULT", codeQR[1]);
                                    }
                                } else {
                                    String[] codeQR = contents.split("/");
                                    //endswith - q.htm or b.htm
                                    // to define weather we need to show category screen or join screen
                                    boolean isCategoryData = contents.endsWith("b.htm");
                                    if (null != scanResult) {
                                        scanResult.barcodeResult(codeQR[3], isCategoryData);
                                    }
                                    Bundle params = new Bundle();
                                    params.putString("codeQR", codeQR[3]);
                                    LaunchActivity.getLaunchActivity().getFireBaseAnalytics().logEvent(AnalyticsEvents.EVENT_SCAN_STORE_CODE_QR_SCREEN, params);
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Failed parsing codeQR reason=" + e.getLocalizedMessage(), e);
                            }
                        } else {
                            Toast toast = Toast.makeText(getActivity(), getString(R.string.error_qrcode_scan), Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    } else {
                        // do nothing
                    }
                }
                Log.d(TAG, "Barcode read: " + contents);
            } else {
                // statusMessage.setText(R.string.barcode_failure);
                Log.d(TAG, "No barcode captured, intent data is null");
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
