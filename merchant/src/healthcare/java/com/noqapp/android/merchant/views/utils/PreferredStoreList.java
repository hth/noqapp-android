package com.noqapp.android.merchant.views.utils;

import com.noqapp.android.common.model.types.BusinessTypeEnum;
import com.noqapp.android.common.model.types.category.HealthCareServiceEnum;
import com.noqapp.android.merchant.presenter.beans.JsonPreferredBusiness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PreferredStoreList {

    private Map<String, List<JsonPreferredBusiness>> preferredBusinessMap = new HashMap<>();

    public PreferredStoreList(List<JsonPreferredBusiness> jsonPreferredBusinessList) {
        for (JsonPreferredBusiness jsonPreferredBusiness : jsonPreferredBusinessList) {
            if (BusinessTypeEnum.PH == jsonPreferredBusiness.getBusinessType()) {
                addToPreferredBusiness(BusinessTypeEnum.PH.name(), jsonPreferredBusiness);
            } else if (BusinessTypeEnum.HS == jsonPreferredBusiness.getBusinessType()) {
                addToPreferredBusiness(jsonPreferredBusiness.getBizCategoryId(), jsonPreferredBusiness);
            }
        }
    }

    public List<JsonPreferredBusiness> getListScan() {
        return null == preferredBusinessMap.get(HealthCareServiceEnum.SCAN.name())
                ? new ArrayList<JsonPreferredBusiness>()
                : preferredBusinessMap.get(HealthCareServiceEnum.SCAN.name());
    }

    public List<JsonPreferredBusiness> getListSono() {
        return null == preferredBusinessMap.get(HealthCareServiceEnum.SONO.name())
                ? new ArrayList<JsonPreferredBusiness>()
                : preferredBusinessMap.get(HealthCareServiceEnum.SONO.name());
    }

    public List<JsonPreferredBusiness> getListPath() {
        return null == preferredBusinessMap.get(HealthCareServiceEnum.SONO.name())
                ? new ArrayList<JsonPreferredBusiness>()
                : preferredBusinessMap.get(HealthCareServiceEnum.SONO.name());
    }

    public List<JsonPreferredBusiness> getListXray() {
        return null == preferredBusinessMap.get(HealthCareServiceEnum.XRAY.name())
                ? new ArrayList<JsonPreferredBusiness>()
                : preferredBusinessMap.get(HealthCareServiceEnum.XRAY.name());
    }

    public List<JsonPreferredBusiness> getListSpec() {
        return null == preferredBusinessMap.get(HealthCareServiceEnum.SPEC.name())
                ? new ArrayList<JsonPreferredBusiness>()
                : preferredBusinessMap.get(HealthCareServiceEnum.SPEC.name());
    }

    public List<JsonPreferredBusiness> getListMri() {
        return null == preferredBusinessMap.get(HealthCareServiceEnum.MRI.name())
                ? new ArrayList<JsonPreferredBusiness>()
                : preferredBusinessMap.get(HealthCareServiceEnum.MRI.name());
    }

    public List<JsonPreferredBusiness> getListMedicine() {
        return null == preferredBusinessMap.get(BusinessTypeEnum.PH.name())
                ? new ArrayList<JsonPreferredBusiness>()
                : preferredBusinessMap.get(BusinessTypeEnum.PH.name());
    }

    public List<JsonPreferredBusiness> getListPhysio() {
        return null == preferredBusinessMap.get(HealthCareServiceEnum.PHYS.name())
                ? new ArrayList<JsonPreferredBusiness>()
                : preferredBusinessMap.get(HealthCareServiceEnum.PHYS.name());
    }

    private void addToPreferredBusiness(String business, final JsonPreferredBusiness jsonPreferredBusiness) {
        if (preferredBusinessMap.containsKey(business)) {
            List<JsonPreferredBusiness> preferredBusinesses = preferredBusinessMap.get(business);
            preferredBusinesses.add(jsonPreferredBusiness);
            preferredBusinessMap.put(business, preferredBusinesses);
        } else {
            List<JsonPreferredBusiness> preferredBusinesses = new LinkedList<JsonPreferredBusiness>() {{
                add(new JsonPreferredBusiness().setDisplayName("Select"));
                add(jsonPreferredBusiness);
            }};
            preferredBusinessMap.put(business, preferredBusinesses);
        }
    }
}
