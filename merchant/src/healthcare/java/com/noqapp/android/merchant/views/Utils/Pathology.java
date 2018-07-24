package com.noqapp.android.merchant.views.Utils;

import java.util.ArrayList;


public class Pathology
{
    private ArrayList<String> blood;
    private ArrayList<String> urine;

    public ArrayList<String> getBlood() {
        return blood;
    }

    public Pathology setBlood(ArrayList<String> blood) {
        this.blood = blood;
        return this;
    }

    public ArrayList<String> getUrine() {
        return urine;
    }

    public Pathology setUrine(ArrayList<String> urine) {
        this.urine = urine;
        return this;
    }
}
