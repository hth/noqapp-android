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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.client.android.CaptureActivity;
import com.noqapp.client.R;
import com.noqapp.client.model.QueueModel;
import com.noqapp.client.presenter.QueuePresenter;
import com.noqapp.client.presenter.beans.JsonQueue;
import com.noqapp.client.utils.AppUtilities;
import com.noqapp.client.utils.Constants;
import com.noqapp.client.utils.Formatter;
import com.noqapp.client.views.activities.BarcodeScannerActivity;
import com.noqapp.client.views.activities.JoinQueueActivity;
import com.noqapp.client.views.activities.LaunchActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ScanQueueFragment extends NoQueueBaseFragment implements QueuePresenter, CaptureActivity.BarcodeScannedResultCallback {

    private final String TAG = ScanQueueFragment.class.getSimpleName();
    private final int CAMERA_AND_STORAGE_PERMISSION_CODE = 102;
    private final String[] CAMERA_AND_STORAGE_PERMISSION_PERMS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };
    @BindView(R.id.tv_store_name)
    protected TextView tv_store_name;
    @BindView(R.id.tv_queue_name)
    protected TextView tv_queue_name;
    @BindView(R.id.tv_address)
    protected TextView tv_address;
    @BindView(R.id.tv_mobile)
    protected TextView tv_mobile;
    @BindView(R.id.tv_total_value)
    protected TextView tv_total_value;
    @BindView(R.id.tv_current_value)
    protected TextView tv_current_value;
    @BindView(R.id.ll_top)
    protected LinearLayout ll_top;
    @BindView(R.id.rl_empty)
    protected RelativeLayout rl_empty;
    @BindView(R.id.btn_joinqueue)
    protected Button btn_joinqueue;
    @BindView(R.id.btnscanQRCode)
    protected Button btnscanQRCode;

    private String codeQr;
    private JsonQueue jsonQueue;


    public ScanQueueFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scan_queue, container, false);
        ButterKnife.bind(this, view);
        BarcodeScannerActivity.barcodeScannedResultCallback = this;
        tv_mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtilities.makeCall(LaunchActivity.getLaunchActivity(), tv_mobile.getText().toString());
            }
        });

        tv_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtilities.openAddressInMap(LaunchActivity.getLaunchActivity(), tv_address.getText().toString());
            }
        });
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


    @Override
    public void queueResponse(JsonQueue jsonQueue) {
        LaunchActivity.getLaunchActivity().dismissProgress();
        Log.d("Queue=", jsonQueue.toString());
        this.jsonQueue = jsonQueue;
        tv_store_name.setText(jsonQueue.getBusinessName());
        tv_queue_name.setText(jsonQueue.getDisplayName());
        tv_address.setText(Formatter.getFormattedAddress(jsonQueue.getStoreAddress()));
        tv_mobile.setText(jsonQueue.getStorePhone());
        tv_total_value.setText(String.valueOf(jsonQueue.getServingNumber()));
        tv_current_value.setText(String.valueOf(jsonQueue.getLastNumber()));
        codeQr = jsonQueue.getCodeQR();
    }

    @Override
    public void queueError() {
        Log.d("Queue=", "Error");
    }

    @OnClick(R.id.btn_joinqueue)
    public void joinQueue() {
        if (null != jsonQueue) {
            Intent intent = new Intent(getActivity(), JoinQueueActivity.class);
            intent.putExtra(JoinQueueActivity.KEY_CODEQR, this.jsonQueue.getCodeQR());
            intent.putExtra(JoinQueueActivity.KEY_DISPLAYNAME, this.jsonQueue.getBusinessName());
            intent.putExtra(JoinQueueActivity.KEY_STOREPHONE, this.jsonQueue.getStorePhone());
            intent.putExtra(JoinQueueActivity.KEY_QUEUENAME, this.jsonQueue.getDisplayName());
            intent.putExtra(JoinQueueActivity.KEY_ADDRESS, this.jsonQueue.getStoreAddress());
            intent.putExtra(JoinQueueActivity.KEY_TOPIC, this.jsonQueue.getTopic());
            getActivity().startActivityForResult(intent, Constants.requestCodeJoinQActivity);


        } else {
            Toast.makeText(getActivity(), "Please scan first", Toast.LENGTH_LONG).show();
        }

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

    private void showEmptyScreen(boolean isShown) {
        if (isShown) {
            rl_empty.setVisibility(View.VISIBLE);
            ll_top.setVisibility(View.GONE);
        } else {
            rl_empty.setVisibility(View.GONE);
            ll_top.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void barcodeScannedResult(String rawData) {
        Log.v("Barcode vaue", rawData.toString());
        if (rawData == null) {
            Log.d("MainActivity", "Cancelled scan");
            Toast.makeText(getActivity(), "Cancelled", Toast.LENGTH_LONG).show();
            showEmptyScreen(true);
        } else {
            if (rawData.startsWith("https://tp.receiptofi.com")) {
                String[] codeQR = rawData.split("/");
//                LaunchActivity.getLaunchActivity().progressDialog.show();
//                QueueModel.queuePresenter = ScanQueueFragment.this;
//                QueueModel.getQueueState(LaunchActivity.DID, codeQR[3]);
//                showEmptyScreen(false);
                Bundle b = new Bundle();
                b.putString(KEY_CODEQR, codeQR[3]);
                b.putBoolean(KEY_FROM_LIST,false);
                JoinFragment jf = new JoinFragment();
                jf.setArguments(b);
                replaceFragmentWithBackStack(getActivity(), R.id.frame_layout, jf, TAG,"");
            } else {
                Toast toast = Toast.makeText(getActivity(), "Not a valid QR-Code", Toast.LENGTH_SHORT);
                toast.show();
                showEmptyScreen(true);
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
