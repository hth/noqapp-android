package com.noqapp.android.merchant.views.activities;

import com.noqapp.android.merchant.R;

import android.os.Bundle;

public class OrderDetailDialog extends OrderDetailActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        isDialog = true;
        super.onCreate(savedInstanceState);
        this.setFinishOnTouchOutside(false);
        actionbarBack.setBackgroundResource(R.drawable.cross);
    }
}
