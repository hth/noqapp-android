package com.noqapp.android.merchant.views.pojos;

import com.google.gson.annotations.SerializedName;
import com.noqapp.android.merchant.utils.Constants;

import java.util.ArrayList;

public class PreferenceObjects {

    @SerializedName(Constants.SYMPTOMS)
    private ArrayList<DataObj> symptomsList = new ArrayList<>();

    public ArrayList<DataObj> getSymptomsList() {
        return symptomsList;
    }

    public PreferenceObjects setSymptomsList(ArrayList<DataObj> symptomsList) {
        this.symptomsList = symptomsList;
        return this;
    }
}
