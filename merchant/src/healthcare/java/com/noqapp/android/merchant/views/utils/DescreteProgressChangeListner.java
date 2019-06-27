package com.noqapp.android.merchant.views.utils;

import android.widget.TextView;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

public class DescreteProgressChangeListner implements DiscreteSeekBar.OnProgressChangeListener{

    DiscreteSeekBar discreteSeekBar;
    TextView tv_label;
    String title = "";

    public DescreteProgressChangeListner(DiscreteSeekBar discreteSeekBar, TextView tv_label, String title) {
        this.discreteSeekBar = discreteSeekBar;
        this.tv_label = tv_label;
        this.title = title;
        this.discreteSeekBar.setOnProgressChangeListener(this);
    }

    @Override
    public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
        tv_label.setText(title + String.valueOf(value));
    }

    @Override
    public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

    }
}
