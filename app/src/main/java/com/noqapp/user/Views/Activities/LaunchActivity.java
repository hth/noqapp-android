package com.noqapp.user.Views.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.client.android.Intents;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.noqapp.user.R;

import java.util.UUID;


public class LaunchActivity extends AppCompatActivity implements View.OnClickListener{

    private Button btnScanner;
    private static String DID = UUID.randomUUID().toString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
       btnScanner = (Button)findViewById(R.id.btnBarcodeScanner);
        btnScanner.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id)
        {
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

    public void scanFromBarcodeScannerFragment()
    {

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public int getCameraId()
    {
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
        if(result != null) {
            if(result.getContents() == null) {
                Log.d("MainActivity", "Cancelled scan");
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Log.d("MainActivity", "Scanned");
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                Log.d("QRCode Result:",result.getContents());
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


}
