package com.noqapp.client.views.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.zxing.client.android.CaptureActivity;
import com.noqapp.client.R;
import com.noqapp.client.views.activities.BarcodeScannerActivity;
import com.noqapp.client.views.activities.LaunchActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ScanQueueFragment extends NoQueueBaseFragment implements CaptureActivity.BarcodeScannedResultCallback {

    private final String TAG = ScanQueueFragment.class.getSimpleName();
    private final int CAMERA_AND_STORAGE_PERMISSION_CODE = 102;
    private final String[] CAMERA_AND_STORAGE_PERMISSION_PERMS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    @BindView(R.id.rl_empty)
    protected RelativeLayout rl_empty;
    @BindView(R.id.btnscanQRCode)
    protected Button btnscanQRCode;

    public ScanQueueFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scan_queue, container, false);
        ButterKnife.bind(this, view);
        BarcodeScannerActivity.barcodeScannedResultCallback = this;
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        startScanningBarcode();

    }

    @Override
    public void onResume() {
        super.onResume();
        LaunchActivity.getLaunchActivity().setActionBarTitle("Home");
    }

    @OnClick(R.id.btnscanQRCode)
    public void scanQR() {
        startScanningBarcode();
    }

    private void startScanningBarcode() {
        if (isCameraAndStoragePermissionAllowed()) {
            scanBarcode();
        } else {
            requestCameraAndStoragePermission();
        }
    }



    @Override
    public void barcodeScannedResult(String rawData) {
        Log.v("Barcode vaue", rawData.toString());
        if (rawData == null) {
            Log.d("MainActivity", "Cancelled scan");
            Toast.makeText(getActivity(), "Cancelled", Toast.LENGTH_LONG).show();

        } else {
            if (rawData.startsWith("https://tp.receiptofi.com")) {
                String[] codeQR = rawData.split("/");
                Bundle b = new Bundle();
                b.putString(KEY_CODEQR, codeQR[3]);
                b.putBoolean(KEY_FROM_LIST,false);
                JoinFragment jf = new JoinFragment();
                jf.setArguments(b);
                replaceFragmentWithBackStack(getActivity(), R.id.frame_layout, jf, TAG,LaunchActivity.tabHome);
            } else {
                Toast toast = Toast.makeText(getActivity(), "Not a valid QR-Code", Toast.LENGTH_SHORT);
                toast.show();

            }

        }
    }
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
