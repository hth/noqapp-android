package com.noqapp.android.merchant.views.pojos;

import com.google.gson.annotations.SerializedName;
import com.noqapp.android.common.utils.CommonHelper;
import com.noqapp.android.merchant.utils.Constants;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class PreferenceObjects {

    @SerializedName("PATH")
    private ArrayList<HCSMenuObject> pathologyList = new ArrayList<>();

    @SerializedName("MRI")
    private ArrayList<HCSMenuObject> mriList = new ArrayList<>();

    @SerializedName("SCAN")
    private ArrayList<HCSMenuObject> scanList = new ArrayList<>();

    @SerializedName("SONO")
    private ArrayList<HCSMenuObject> sonoList = new ArrayList<>();

    @SerializedName("XRAY")
    private ArrayList<HCSMenuObject> xrayList = new ArrayList<>();

    @SerializedName("SPEC")
    private ArrayList<HCSMenuObject> specList = new ArrayList<>();

    @SerializedName(Constants.SYMPTOMS)
    private ArrayList<DataObj> symptomsList = new ArrayList<>();

    @SerializedName("LASTUPDATE")
    private String lastUpdateDate = "";


    public ArrayList<HCSMenuObject> getPathologyList() {
        return pathologyList;
    }

    public PreferenceObjects setPathologyList(ArrayList<HCSMenuObject> pathologyList) {
        this.pathologyList = pathologyList;
        return this;
    }

    public ArrayList<HCSMenuObject> getMriList() {
        return mriList;
    }

    public PreferenceObjects setMriList(ArrayList<HCSMenuObject> mriList) {
        this.mriList = mriList;
        return this;
    }

    public ArrayList<HCSMenuObject> getScanList() {
        return scanList;
    }

    public PreferenceObjects setScanList(ArrayList<HCSMenuObject> scanList) {
        this.scanList = scanList;
        return this;
    }

    public ArrayList<HCSMenuObject> getSonoList() {
        return sonoList;
    }

    public PreferenceObjects setSonoList(ArrayList<HCSMenuObject> sonoList) {
        this.sonoList = sonoList;
        return this;
    }

    public ArrayList<HCSMenuObject> getXrayList() {
        return xrayList;
    }

    public PreferenceObjects setXrayList(ArrayList<HCSMenuObject> xrayList) {
        this.xrayList = xrayList;
        return this;
    }

    public ArrayList<HCSMenuObject> getSpecList() {
        return specList;
    }

    public PreferenceObjects setSpecList(ArrayList<HCSMenuObject> specList) {
        this.specList = specList;
        return this;
    }

    public ArrayList<DataObj> getSymptomsList() {
        return symptomsList;
    }

    public PreferenceObjects setSymptomsList(ArrayList<DataObj> symptomsList) {
        this.symptomsList = symptomsList;
        return this;
    }

    public String getLastUpdateDate() {
        return lastUpdateDate;
    }

    public PreferenceObjects setLastUpdateDate(String lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
        return this;
    }

    public ArrayList<HCSMenuObject> clearListSelection(ArrayList<HCSMenuObject> selectList) {
        ArrayList<HCSMenuObject> temp = new ArrayList<>();
        for (HCSMenuObject d :
                selectList) {
            temp.add(d.setSelect(false));
        }
        return temp;
    }


    public boolean isLastUpdateTimeExceed(){
        Calendar now = Calendar.getInstance();
        boolean monday = now.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY;
        Date startDate = null;
        try {
            startDate = CommonHelper.SDF_YYYY_MM_DD.parse(lastUpdateDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date endDate = new Date();
        long duration = endDate.getTime() - startDate.getTime();
        long diffInDays = TimeUnit.MILLISECONDS.toDays(duration);
        return diffInDays > 7 || monday;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("PreferenceObjects{");
        sb.append("pathologyList=").append(pathologyList);
        sb.append(", mriList=").append(mriList);
        sb.append(", scanList=").append(scanList);
        sb.append(", sonoList=").append(sonoList);
        sb.append(", xrayList=").append(xrayList);
        sb.append(", specList=").append(specList);
        sb.append(", lastUpdateDate='").append(lastUpdateDate).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
