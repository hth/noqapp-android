package com.noqapp.android.merchant.views.utils;

import com.google.gson.annotations.SerializedName;
import com.noqapp.android.merchant.views.pojos.DataObj;

import java.util.ArrayList;

public class TestCaseObjects {

    @SerializedName("PATH")
    private ArrayList<DataObj> pathologyList = new ArrayList<>();
    @SerializedName("MRI")
    private ArrayList<DataObj> mriList = new ArrayList<>();
    @SerializedName("SCAN")
    private ArrayList<DataObj> scanList = new ArrayList<>();
    @SerializedName("SONO")
    private ArrayList<DataObj> sonoList = new ArrayList<>();
    @SerializedName("XRAY")
    private ArrayList<DataObj> xrayList = new ArrayList<>();

    public ArrayList<DataObj> getPathologyList() {
        return pathologyList;
    }

    public void setPathologyList(ArrayList<DataObj> pathologyList) {
        this.pathologyList = pathologyList;
    }

    public ArrayList<DataObj> getMriList() {
        return mriList;
    }

    public void setMriList(ArrayList<DataObj> mriList) {
        this.mriList = mriList;
    }

    public ArrayList<DataObj> getScanList() {
        return scanList;
    }

    public void setScanList(ArrayList<DataObj> scanList) {
        this.scanList = scanList;
    }

    public ArrayList<DataObj> getSonoList() {
        return sonoList;
    }

    public void setSonoList(ArrayList<DataObj> sonoList) {
        this.sonoList = sonoList;
    }

    public ArrayList<DataObj> getXrayList() {
        return xrayList;
    }

    public void setXrayList(ArrayList<DataObj> xrayList) {
        this.xrayList = xrayList;
    }
}
