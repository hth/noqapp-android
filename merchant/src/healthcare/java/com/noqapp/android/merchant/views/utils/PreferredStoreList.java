package com.noqapp.android.merchant.views.utils;



import com.noqapp.android.common.model.types.BusinessTypeEnum;
import com.noqapp.android.common.model.types.category.HealthCareServiceEnum;
import com.noqapp.android.merchant.presenter.beans.JsonPreferredBusiness;

import java.util.ArrayList;
import java.util.List;

public class PreferredStoreList {

    private List<JsonPreferredBusiness> ListScan = new ArrayList<>();
    private List<JsonPreferredBusiness> ListSono = new ArrayList<>();
    private List<JsonPreferredBusiness> ListPath = new ArrayList<>();
    private List<JsonPreferredBusiness> ListXray = new ArrayList<>();
    private List<JsonPreferredBusiness> ListSpec = new ArrayList<>();
    private List<JsonPreferredBusiness> ListMri = new ArrayList<>();
    private List<JsonPreferredBusiness> ListMedicine = new ArrayList<>();
    private List<JsonPreferredBusiness> ListPhysio = new ArrayList<>();

    public PreferredStoreList(List<JsonPreferredBusiness> jsonPreferredBusinessList ) {
        initCheckBoxList(jsonPreferredBusinessList);
    }


    private void initCheckBoxList(List<JsonPreferredBusiness> jsonPreferredBusinessList) {
       resetList();
    
        for (int i = 0; i < jsonPreferredBusinessList.size(); i++) {
            JsonPreferredBusiness jpb = jsonPreferredBusinessList.get(i);
            if (jpb.getBusinessType() == BusinessTypeEnum.PH) {
                ListMedicine.add(jpb);
            } else if (jpb.getBusinessType() == BusinessTypeEnum.HS) {

                HealthCareServiceEnum hcse = HealthCareServiceEnum.valueOf(jpb.getBizCategoryId());
                switch (hcse) {
                    case SONO:
                        ListSono.add(jpb);
                        break;
                    case SCAN:
                        ListScan.add(jpb);
                        break;
                    case MRI:
                        ListMri.add(jpb);
                        break;
                    case PATH:
                        ListPath.add(jpb);
                        break;
                    case XRAY:
                        ListXray.add(jpb);
                        break;
                    case SPEC:
                        ListSpec.add(jpb);
                        break;
                    case PHYS:
                        ListPhysio.add(jpb);
                        break;
                }
            }
        }
    }

    public List<JsonPreferredBusiness> getListScan() {
        return ListScan;
    }

    public List<JsonPreferredBusiness> getListSono() {
        return ListSono;
    }

    public List<JsonPreferredBusiness> getListPath() {
        return ListPath;
    }

    public List<JsonPreferredBusiness> getListXray() {
        return ListXray;
    }

    public List<JsonPreferredBusiness> getListSpec() {
        return ListSpec;
    }

    public List<JsonPreferredBusiness> getListMri() {
        return ListMri;
    }

    public List<JsonPreferredBusiness> getListMedicine() {
        return ListMedicine;
    }

    public List<JsonPreferredBusiness> getListPhysio() {
        return ListPhysio;
    }
    private void resetList(){
        ListScan.clear();
        ListSono.clear();
        ListPath.clear();
        ListXray.clear();
        ListSpec.clear();
        ListMri.clear();
        ListMedicine.clear();
        ListScan.add(0, new JsonPreferredBusiness().setDisplayName("Select"));
        ListSono.add(0, new JsonPreferredBusiness().setDisplayName("Select"));
        ListPath.add(0, new JsonPreferredBusiness().setDisplayName("Select"));
        ListXray.add(0, new JsonPreferredBusiness().setDisplayName("Select"));
        ListSpec.add(0, new JsonPreferredBusiness().setDisplayName("Select"));
        ListMri.add(0, new JsonPreferredBusiness().setDisplayName("Select"));
        ListMedicine.add(0, new JsonPreferredBusiness().setDisplayName("Select"));
    }
}
