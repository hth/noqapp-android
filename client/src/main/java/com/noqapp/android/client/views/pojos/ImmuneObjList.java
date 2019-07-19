package com.noqapp.android.client.views.pojos;

import com.noqapp.android.common.beans.medical.JsonImmunization;

import java.io.Serializable;
import java.util.ArrayList;

public class ImmuneObjList implements Serializable {

    private String headerTitle;
    private ArrayList<JsonImmunization> immuneObjs = new ArrayList<>();

    public String getHeaderTitle() {
        return headerTitle;
    }

    public void setHeaderTitle(String headerTitle) {
        this.headerTitle = headerTitle;
    }

    public ArrayList<JsonImmunization> getImmuneObjs() {
        return immuneObjs;
    }

    public void setImmuneObjs(ArrayList<JsonImmunization> immuneObjs) {
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
