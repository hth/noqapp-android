package com.noqapp.android.merchant.views.pojos;

public class DataObj {
    private String name;
    private String category;
    private boolean isSelect = false;

    public DataObj() {
    }

    public DataObj(String name, boolean isSelect) {
        this.name = name;
        this.category = "";
        this.isSelect = isSelect;
    }

    public DataObj(String name, String category, boolean isSelect) {
        this.name = name;
        this.category = category;
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

    public String getCategory() {
        return category;
    }

    public DataObj setCategory(String category) {
        this.category = category;
        return this;
    }

    @Override
    public String toString() {
        return "DataObj{" +
                "name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", isSelect=" + isSelect +
                '}';
    }
}
