package com.noqapp.android.merchant.views.pojos;

import com.noqapp.android.merchant.presenter.beans.JsonMasterLab;

public class HCSMenuObject implements Comparable<HCSMenuObject> {
    private boolean isSelect = false;
    private JsonMasterLab jsonMasterLab;
    private double price = 0.0;

    public boolean isSelect() {
        return isSelect;
    }

    public HCSMenuObject setSelect(boolean select) {
        isSelect = select;
        return this;
    }

    public JsonMasterLab getJsonMasterLab() {
        return jsonMasterLab;
    }

    public HCSMenuObject setJsonMasterLab(JsonMasterLab jsonMasterLab) {
        this.jsonMasterLab = jsonMasterLab;
        return this;
    }

    public double getPrice() {
        return price;
    }

    public HCSMenuObject setPrice(double price) {
        this.price = price;
        return this;
    }

    public int compareTo(HCSMenuObject other) {
        return jsonMasterLab.getProductShortName().compareTo(other.getJsonMasterLab().getProductShortName());
    }

    public String getSortName() {
        return toCamelCase(jsonMasterLab.getProductShortName());
    }

    @Override
    public boolean equals(Object obj) {
        return (this.jsonMasterLab.getProductShortName().equals(((HCSMenuObject) obj).getJsonMasterLab().getProductShortName()));
    }

    private String toCamelCase(final String init) {
        if (init == null)
            return null;

        final StringBuilder ret = new StringBuilder(init.length());

        for (final String word : init.split(" ")) {
            if (!word.isEmpty()) {
                ret.append(word.substring(0, 1).toUpperCase());
                ret.append(word.substring(1).toLowerCase());
            }
            if (!(ret.length() == init.length()))
                ret.append(" ");
        }

        return ret.toString();
    }

}
