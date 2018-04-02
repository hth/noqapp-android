package com.noqapp.android.client.views.toremove;

import android.text.TextUtils;

import com.noqapp.android.client.presenter.beans.JsonStoreProduct;

import java.io.Serializable;

/**
 * Created by chandra on 3/28/18.
 */

public class ChildData implements Serializable {

    private String childInput;
    private JsonStoreProduct jsonStoreProduct;


    public ChildData(String childInput, JsonStoreProduct jsonStoreProduct) {
        this.childInput = childInput;
        this.jsonStoreProduct = jsonStoreProduct;
    }


    public String getChildInput() {
        return TextUtils.isEmpty(childInput) ? "0" : childInput;
    }

    public ChildData setChildInput(String childInput) {
        this.childInput = childInput;
        return this;
    }

    public JsonStoreProduct getJsonStoreProduct() {
        return jsonStoreProduct;
    }

    public ChildData setJsonStoreProduct(JsonStoreProduct jsonStoreProduct) {
        this.jsonStoreProduct = jsonStoreProduct;
        return this;
    }

    @Override
    public String toString() {
        return "ChildData{" +
                " childInput='" + childInput + '\'' +
                ", jsonStoreProduct=" + jsonStoreProduct +
                '}';
    }
}
