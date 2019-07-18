package com.noqapp.android.merchant.views.pojos;

import java.io.Serializable;
import java.util.ArrayList;

public class ImmuneObjList implements Serializable {

    private String headerTitle;
    private ArrayList<ImmuneObj> immuneObjs = new ArrayList<>();

    public String getHeaderTitle() {
        return headerTitle;
    }

    public void setHeaderTitle(String headerTitle) {
        this.headerTitle = headerTitle;
    }

    public ArrayList<ImmuneObj> getImmuneObjs() {
        return immuneObjs;
    }

    public void setImmuneObjs(ArrayList<ImmuneObj> immuneObjs) {
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
