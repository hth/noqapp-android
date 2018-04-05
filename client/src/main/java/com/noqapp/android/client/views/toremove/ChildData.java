package com.noqapp.android.client.views.toremove;


import com.noqapp.android.client.presenter.beans.JsonStoreProduct;

import java.io.Serializable;

/**
 * Created by chandra on 3/28/18.
 */

public class ChildData implements Serializable {

    private int childInput;
    private JsonStoreProduct jsonStoreProduct;


    public ChildData(int childInput, JsonStoreProduct jsonStoreProduct) {
        this.childInput = childInput;
        this.jsonStoreProduct = jsonStoreProduct;
    }


    public int getChildInput() {
        return childInput;
    }

    public ChildData setChildInput(int childInput) {
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
