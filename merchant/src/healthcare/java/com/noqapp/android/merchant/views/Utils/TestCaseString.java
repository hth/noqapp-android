package com.noqapp.android.merchant.views.Utils;

import java.util.ArrayList;

public class TestCaseString {




    private Pathology pathology;
    private Radiology radiology;
    private ArrayList<String> general;

    public Pathology getPathology() {
        return pathology;
    }

    public TestCaseString setPathology(Pathology pathology) {
        this.pathology = pathology;
        return this;
    }

    public Radiology getRadiology() {
        return radiology;
    }

    public TestCaseString setRadiology(Radiology radiology) {
        this.radiology = radiology;
        return this;
    }

    public ArrayList<String> getGeneral() {
        return general;
    }

    public TestCaseString setGeneral(ArrayList<String> general) {
        this.general = general;
        return this;
    }
}
