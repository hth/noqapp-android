package com.noqapp.android.merchant.views.interfaces;

import com.noqapp.android.common.beans.medical.JsonMedicalMedicine;

public interface AdapterCommunicate{

    void updateFavouriteList(JsonMedicalMedicine jsonMedicalMedicine, boolean isAdded);

    // isAdded ? add the item : update the item
    void updateNonFavouriteList(JsonMedicalMedicine jsonMedicalMedicine, boolean isAdded);

}
