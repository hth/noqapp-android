package com.noqapp.android.merchant.views.pojos;

import com.noqapp.android.merchant.presenter.beans.JsonPreferredBusiness;

public class CheckBoxObj {

    private boolean isSelect = false;
    private JsonPreferredBusiness jsonPreferredBusiness;

    public boolean isSelect() {
        return isSelect;
    }

    public CheckBoxObj setSelect(boolean select) {
        isSelect = select;
        return this;
    }

    public JsonPreferredBusiness getJsonPreferredBusiness() {
        return jsonPreferredBusiness;
    }

    public CheckBoxObj setJsonPreferredBusiness(JsonPreferredBusiness jsonPreferredBusiness) {
        this.jsonPreferredBusiness = jsonPreferredBusiness;
        return this;
    }
}
