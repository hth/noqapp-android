package com.noqapp.android.merchant.views.activities;

import android.os.Bundle;
import android.view.Window;

/**
 * Created by chandra on 7/22/17.
 */

public class SettingDialogActivity extends SettingActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        this.setFinishOnTouchOutside(false);
    }
}