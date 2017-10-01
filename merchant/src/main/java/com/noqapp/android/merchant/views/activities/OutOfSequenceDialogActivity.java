package com.noqapp.android.merchant.views.activities;

import android.os.Bundle;

import com.noqapp.android.merchant.R;

/**
 * Created by chandra on 7/22/17.
 */

public class OutOfSequenceDialogActivity extends OutOfSequenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        isDialog = true;
        super.onCreate(savedInstanceState);
        this.setFinishOnTouchOutside(false);
        actionbarBack.setBackgroundResource(R.drawable.cross);
    }
}