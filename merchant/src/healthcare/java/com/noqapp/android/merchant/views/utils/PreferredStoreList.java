package com.noqapp.android.merchant.views.utils;

import com.noqapp.android.common.model.types.BusinessTypeEnum;
import com.noqapp.android.common.model.types.category.HealthCareServiceEnum;
import com.noqapp.android.merchant.presenter.beans.JsonPreferredBusiness;

import java.util.ArrayList;
import java.util.List;

public class PreferredStoreList {

    private List<JsonPreferredBusiness> scanBusinesses = new ArrayList<>();
    private List<JsonPreferredBusiness> sonoBusinesses = new ArrayList<>();
    private List<JsonPreferredBusiness> pathBusinesses = new ArrayList<>();
    private List<JsonPreferredBusiness> xrayBusinesses = new ArrayList<>();
    private List<JsonPreferredBusiness> specBusinesses = new ArrayList<>();
    private List<JsonPreferredBusiness> mriBusinesses = new ArrayList<>();
    private List<JsonPreferredBusiness> pharmacyBusinesses = new ArrayList<>();
    private List<JsonPreferredBusiness> physioBusinesses = new ArrayList<>();

    public PreferredStoreList(List<JsonPreferredBusiness> jsonPreferredBusinessList) {
        initCheckBoxList(jsonPreferredBusinessList);
    }

    private void initCheckBoxList(List<JsonPreferredBusiness> jsonPreferredBusinessList) {
        resetList();

        for (JsonPreferredBusiness jsonPreferredBusiness : jsonPreferredBusinessList) {
            if (BusinessTypeEnum.PH == jsonPreferredBusiness.getBusinessType()) {
                pharmacyBusinesses.add(jsonPreferredBusiness);
            } else if (BusinessTypeEnum.HS == jsonPreferredBusiness.getBusinessType()) {
                switch (HealthCareServiceEnum.valueOf(jsonPreferredBusiness.getBizCategoryId())) {
                    case SONO:
                        sonoBusinesses.add(jsonPreferredBusiness);
                        break;
                    case SCAN:
                        scanBusinesses.add(jsonPreferredBusiness);
                        break;
                    case MRI:
                        mriBusinesses.add(jsonPreferredBusiness);
                        break;
                    case PATH:
                        pathBusinesses.add(jsonPreferredBusiness);
                        break;
                    case XRAY:
                        xrayBusinesses.add(jsonPreferredBusiness);
                        break;
                    case SPEC:
                        specBusinesses.add(jsonPreferredBusiness);
                        break;
                    case PHYS:
                        physioBusinesses.add(jsonPreferredBusiness);
                        break;
                }
            }
        }
    }

    public List<JsonPreferredBusiness> getListScan() {
        return scanBusinesses;
    }

    public List<JsonPreferredBusiness> getListSono() {
        return sonoBusinesses;
    }

    public List<JsonPreferredBusiness> getListPath() {
        return pathBusinesses;
    }

    public List<JsonPreferredBusiness> getListXray() {
        return xrayBusinesses;
    }

    public List<JsonPreferredBusiness> getListSpec() {
        return specBusinesses;
    }

    public List<JsonPreferredBusiness> getListMri() {
        return mriBusinesses;
    }

    public List<JsonPreferredBusiness> getListMedicine() {
        return pharmacyBusinesses;
    }

    public List<JsonPreferredBusiness> getListPhysio() {
        return physioBusinesses;
    }

    private void resetList() {
        scanBusinesses.clear();
        sonoBusinesses.clear();
        pathBusinesses.clear();
        xrayBusinesses.clear();
        specBusinesses.clear();
        mriBusinesses.clear();
        pharmacyBusinesses.clear();

        JsonPreferredBusiness select = new JsonPreferredBusiness().setDisplayName("Select");
        scanBusinesses.add(0, select);
        sonoBusinesses.add(0, select);
        pathBusinesses.add(0, select);
        xrayBusinesses.add(0, select);
        specBusinesses.add(0, select);
        mriBusinesses.add(0, select);
        pharmacyBusinesses.add(0, select);
    }
}
