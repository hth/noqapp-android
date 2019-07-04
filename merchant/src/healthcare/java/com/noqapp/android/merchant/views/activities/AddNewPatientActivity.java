package com.noqapp.android.merchant.views.activities;

import android.os.Bundle;

public class AddNewPatientActivity extends LoginActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tv_toolbar_title.setText("Add New Patient");
    }
}
