package com.noqapp.android.merchant.views.utils;

import android.view.View;
import android.widget.CompoundButton;

public class CompoundCheckChangedListener implements CompoundButton.OnCheckedChangeListener{
    private View view;
    public CompoundCheckChangedListener(View view){
        this.view =view;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            view.setVisibility(View.GONE);
        } else {
            view.setVisibility(View.VISIBLE);
        }
    }
}