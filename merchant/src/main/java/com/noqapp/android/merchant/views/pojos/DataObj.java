package com.noqapp.android.merchant.views.pojos;

public class DataObj implements Comparable<DataObj> {
    private String shortName;
    private String fullName;
    private String category;
    private boolean isSelect = false;

    public DataObj() {
    }

    public DataObj(String shortName, boolean isSelect) {
        this.shortName = shortName;
        fullName = shortName;
        this.category = "";
        this.isSelect = isSelect;
    }

    public DataObj(String shortName, String category, boolean isSelect) {
        this.shortName = shortName;
        fullName = shortName;
        this.category = category;
        this.isSelect = isSelect;
    }

    public DataObj(String fullName, String shortName, String category, boolean isSelect) {
        this.shortName = shortName;
        this.fullName = fullName;
        this.category = category;
        this.isSelect = isSelect;
    }

    public String getShortName() {
        return shortName;
    }

    public DataObj setShortName(String shortName) {
        this.shortName = shortName;
        return this;
    }

    public String getFullName() {
        return fullName;
    }

    public DataObj setFullName(String fullName) {
        this.fullName = fullName;
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
                "shortName='" + shortName + '\'' +
                ", fullName='" + fullName + '\'' +
                ", category='" + category + '\'' +
                ", isSelect=" + isSelect +
                '}';
    }

    public int compareTo(DataObj other) {
        return shortName.compareTo(other.shortName);
    }

    @Override
    public boolean equals(Object obj) {
        return (this.shortName.equals(((DataObj) obj).shortName));
    }
}
