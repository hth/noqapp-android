package com.noqapp.client.views.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.zxing.client.android.CaptureActivity;
import com.noqapp.client.R;


public class BarcodeScannerActivity extends CaptureActivity {

    @Override
    public void onCreate(Bundle bundle) {
        // TODO Auto-generated method stub
        super.onCreate(bundle);
        Button btnAnnuler = (Button) findViewById(R.id.btnClose);

        btnAnnuler.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                finish();
            }
        });
    }


    @Override
    protected boolean showHelpOnFirstLaunch() {
        // TODO Auto-generated method stub
        return false;
    }
}
