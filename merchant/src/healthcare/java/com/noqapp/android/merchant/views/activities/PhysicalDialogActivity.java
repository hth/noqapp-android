package com.noqapp.android.merchant.views.activities;

import android.os.Bundle;

import com.noqapp.android.merchant.R;


public class PhysicalDialogActivity extends PhysicalActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        isDialog = true;
        super.onCreate(savedInstanceState);
        this.setFinishOnTouchOutside(false);
        actionbarBack.setBackgroundResource(R.drawable.cross);
    }
}