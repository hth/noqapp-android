package com.noqapp.android.merchant.views.Utils;

import java.util.ArrayList;

public class Radiology
{
    private ArrayList<String> x_ray;
    private ArrayList<String> mri;

    public ArrayList<String> getX_ray() {
        return x_ray;
    }

    public Radiology setX_ray(ArrayList<String> x_ray) {
        this.x_ray = x_ray;
        return this;
    }

    public ArrayList<String> getMri() {
        return mri;
    }

    public Radiology setMri(ArrayList<String> mri) {
        this.mri = mri;
        return this;
    }
}