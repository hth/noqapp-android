package com.noqapp.client.views.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.noqapp.client.R;
import com.noqapp.client.model.QueueModel;
import com.noqapp.client.presenter.QueuePresenter;
import com.noqapp.client.presenter.beans.JsonQueue;
import com.noqapp.client.views.activities.JoinQueueActivity;
import com.noqapp.client.views.activities.LaunchActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ScanQueueFragment extends Fragment implements QueuePresenter {

    private static final String TAG = ScanQueueFragment.class.getSimpleName();
    @BindView(R.id.txtBusinessName)
    protected TextView txtBusinessName;
    @BindView(R.id.txtStoreAddress)
    protected TextView txtStoreAddress;
    @BindView(R.id.txtStorePhone)
    protected TextView txtStorePhone;
    @BindView(R.id.txtServingNumber)
    protected TextView txtServingNumber;
    @BindView(R.id.txtLastNumber)
    protected TextView txtLastNumber;
    @BindView(R.id.txtDisplayName)
    protected TextView txtDisplayName;
    private String codeQr;
    private JsonQueue jsonQueue;


    public ScanQueueFragment() {
        // Required empty public constructor
    }

    public static Fragment getInstance() {
        return new ScanQueueFragment();
    }


    public void startScanningBarcode(Activity context) {
        IntentIntegrator integrator = new IntentIntegrator(context);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scan");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(false);
        integrator.forSupportFragment(this).initiateScan();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_scan_queue, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i(TAG, "onActivityCreated ::");

    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "onStart ::");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume ::");
        LaunchActivity.getLaunchActivity().setActionBarTitle("Home");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "onResume ::");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Log.d("MainActivity", "Cancelled scan");
                Toast.makeText(getActivity(), "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Log.d("MainActivity", "Scanned");
                Toast.makeText(getActivity(), "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                Log.d("QRCode Result:", result.getContents());


                if (result.getContents().startsWith("https://tp.receiptofi.com")) {
                    String[] codeQR = result.getContents().split("/");
                    QueueModel.queuePresenter = ScanQueueFragment.this;
                    QueueModel.getQueueState(LaunchActivity.DID, codeQR[3]);
                } else {
                    Toast toast = Toast.makeText(getActivity(), "No scan data received!", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    @Override
    public void queueResponse(JsonQueue jsonQueue) {
        Log.d("Queue=", jsonQueue.toString());
        this.jsonQueue = jsonQueue;
        txtBusinessName.setText(jsonQueue.getBusinessName());
        txtDisplayName.setText(jsonQueue.getDisplayName());
        txtStoreAddress.setText(jsonQueue.getStoreAddress());
        txtStorePhone.setText(jsonQueue.getStorePhone());
        txtLastNumber.setText(String.valueOf(jsonQueue.getLastNumber()));
        txtServingNumber.setText(String.valueOf(jsonQueue.getServingNumber()));
        codeQr = jsonQueue.getCodeQR();
    }

    @Override
    public void queueError() {
        Log.d("Queue=", "Error");
    }

    @OnClick(R.id.btn_joinqueue)
    public void joinQueue(View view) {

        Intent intent = new Intent(getActivity(), JoinQueueActivity.class);
        intent.putExtra(JoinQueueActivity.KEY_CODEQR, this.jsonQueue.getCodeQR());
        intent.putExtra(JoinQueueActivity.KEY_DISPLAYNAME, this.jsonQueue.getDisplayName());
        intent.putExtra(JoinQueueActivity.KEY_STOREPHONE, this.jsonQueue.getStorePhone());
        intent.putExtra(JoinQueueActivity.KEY_QUEUENAME, this.jsonQueue.getBusinessName());
        startActivity(intent);

//        LaunchActivity.tempViewpager.setCurrentItem(1);
//        ListQueueFragment queueFragment = new ListQueueFragment();
//        queueFragment.codeQR = codeQr;
//        queueFragment.callQueue();

    }

    @OnClick(R.id.btnscanQRCode)
    public void scanQR(View view) {
        IntentIntegrator integrator = new IntentIntegrator(getActivity());
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scan");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(false);
        integrator.forSupportFragment(this).initiateScan();
    }
}
