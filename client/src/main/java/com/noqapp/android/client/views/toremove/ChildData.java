package com.noqapp.android.client.views.toremove;

import android.text.TextUtils;

/**
 * Created by chandra on 3/28/18.
 */

public class ChildData {
    private String childTitle;
    private String childInput;

    public ChildData(String childTitle, String childInput) {
        this.childTitle = childTitle;
        this.childInput = childInput;
    }

    public ChildData(String childTitle) {
        this.childTitle = childTitle;
    }

    public String getChildTitle() {
        return childTitle;
    }

    public ChildData setChildTitle(String childTitle) {
        this.childTitle = childTitle;
        return this;
    }

    public String getChildInput() {
        return TextUtils.isEmpty(childInput)?"0":childInput;
    }

    public ChildData setChildInput(String childInput) {
        this.childInput = childInput;
        return this;
    }

    @Override
    public String toString() {
        return "ChildData{" +
                "childTitle='" + childTitle + '\'' +
                ", childInput='" + childInput + '\'' +
                '}';
    }
}
