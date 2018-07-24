package com.noqapp.android.merchant.views.Utils;

public class GridItem {
    private String label;
    private String key;
    private boolean isSelect;
    private boolean isFavourite;

    public String getLabel() {
        return label;
    }

    public GridItem setLabel(String label) {
        this.label = label;
        return this;
    }

    public String getKey() {
        return key;
    }

    public GridItem setKey(String key) {
        this.key = key;
        return this;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public GridItem setSelect(boolean select) {
        isSelect = select;
        return this;
    }

    public boolean isFavourite() {
        return isFavourite;
    }

    public GridItem setFavourite(boolean favourite) {
        isFavourite = favourite;
        return this;
    }
}
