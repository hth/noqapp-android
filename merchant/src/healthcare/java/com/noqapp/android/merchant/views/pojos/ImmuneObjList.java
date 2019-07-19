package com.noqapp.android.merchant.views.pojos;

import com.noqapp.android.common.beans.medical.JsonHospitalVisitSchedule;

import java.io.Serializable;
import java.util.ArrayList;

public class ImmuneObjList implements Serializable {

    private String headerTitle;
    private ArrayList<JsonHospitalVisitSchedule> immuneObjs = new ArrayList<>();

    public String getHeaderTitle() {
        return headerTitle;
    }

    public void setHeaderTitle(String headerTitle) {
        this.headerTitle = headerTitle;
    }

    public ArrayList<JsonHospitalVisitSchedule> getImmuneObjs() {
        return immuneObjs;
    }

    public void setImmuneObjs(ArrayList<JsonHospitalVisitSchedule> immuneObjs) {
        this.immuneObjs = immuneObjs;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ImmuneObjList{");
        sb.append("headerTitle='").append(headerTitle).append('\'');
        sb.append(", immuneObjs=").append(immuneObjs);
        sb.append('}');
        return sb.toString();
    }

}
