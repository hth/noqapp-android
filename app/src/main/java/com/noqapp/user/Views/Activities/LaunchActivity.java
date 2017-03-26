package com.noqapp.user.Views.Activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.noqapp.user.Model.ScanQRCodeModel;
import com.noqapp.user.Presenter.Beans.ScanQRCode;
import com.noqapp.user.Presenter.QRCodePresenter;
import com.noqapp.user.R;

import org.w3c.dom.Text;

import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class LaunchActivity extends AppCompatActivity implements View.OnClickListener, QRCodePresenter {

    @BindView(R.id.txtBussinessName)TextView txtBussinessName;
    @BindView(R.id.txtStoreAddress)TextView txtStoreAddress;
    @BindView(R.id.txtStorePhone)TextView txtStorePhone;
    @BindView(R.id.txtServiceLabel)TextView txtServiceLabel;
    @BindView(R.id.txtPeopleInQ)TextView txtPleopleInQ;
    @BindView(R.id.txtDisplayName)TextView txtDisplayName;


    private static String DID = UUID.randomUUID().toString();
    private Button btnScanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        btnScanner = (Button) findViewById(R.id.btnBarcodeScanner);
        btnScanner.setOnClickListener(this);
        ButterKnife.bind(this);

    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btnBarcodeScanner:
                IntentIntegrator integrator = new IntentIntegrator(LaunchActivity.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                integrator.setPrompt("Scan");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();
                break;
        }

    }

    @OnClick(R.id.btnJoin)
    public void join(View view)
    {

    }

    public void scanFromBarcodeScannerFragment() {

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public int getCameraId() {
//        int cameraId = -1;
//        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
//        try {
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//                String[] numberOfCamera = manager.getCameraIdList();
//
//                for (int i = 0; i < numberOfCamera.length; i++) {
//                    CameraInfo info = new CameraInfo();
//                    Camera.getCameraInfo(i, info);
//                    if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
//                        Log.d(TAG, "Camera found");
//                        cameraId = i;
//                        break;
//                    }
//                }
//                return cameraId;
//
//            }
//        } catch (CameraAccessException e) {
//            e.printStackTrace();
//        }
        return 0;
    }

    public void tabs(View view) {
        Intent intent = new Intent(this, TabbedScanning.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Log.d("MainActivity", "Cancelled scan");
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Log.d("MainActivity", "Scanned");
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                Log.d("QRCode Result:", result.getContents());

                String qrcode = result.getContents();
                ScanQRCodeModel model = new ScanQRCodeModel();
                model.presenter = this;
                model.getQRCodeResponse(DID, "A", "58d75f4d51bf63ca840f529c");
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void didQRCodeResponse(ScanQRCode qrCode) {
        Log.d("QRCode Response :", qrCode.toString());
        txtBussinessName.setText(qrCode.getN());
        txtDisplayName.setText(qrCode.getD());
        txtStoreAddress.setText(qrCode.getSa());
        txtStorePhone.setText(qrCode.getP());
        txtPleopleInQ.setText(String.valueOf(qrCode.getL()));
        txtServiceLabel.setText(String.valueOf(qrCode.getS()));
    }

    @Override
    public void didQRCodeError() {
        Log.d("QRCodeError", "Error");

    }
}
