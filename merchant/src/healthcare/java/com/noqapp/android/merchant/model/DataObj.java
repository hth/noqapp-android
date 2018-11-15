package com.noqapp.android.merchant.model;

public class DataObj {
    private String name;
    private boolean isSelect = false;

    public DataObj() {
    }

    public DataObj(String name, boolean isSelect) {
        this.name = name;
        this.isSelect = isSelect;
    }

    public String getName() {
        return name;
    }

    public DataObj setName(String name) {
        this.name = name;
        return this;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public DataObj setSelect(boolean select) {
        isSelect = select;
        return this;
    }
}
