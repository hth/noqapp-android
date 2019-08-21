package com.noqapp.android.merchant.views.pojos;

public class TempNeuroObj {
    private String title = "";
    private String rightValue = "";
    private String leftValue = "";
    private boolean isHeader = false;

    public String getTitle() {
        return title;
    }

    public TempNeuroObj setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getRightValue() {
        return rightValue;
    }

    public TempNeuroObj setRightValue(String rightValue) {
        this.rightValue = rightValue;
        return this;
    }

    public String getLeftValue() {
        return leftValue;
    }

    public TempNeuroObj setLeftValue(String leftValue) {
        this.leftValue = leftValue;
        return this;
    }

    public boolean isHeader() {
        return isHeader;
    }

    public TempNeuroObj setHeader(boolean header) {
        isHeader = header;
        return this;
    }

    public TempNeuroObj(String title, String rightValue, String leftValue, boolean isHeader) {
        this.title = title;
        this.rightValue = rightValue;
        this.leftValue = leftValue;
        this.isHeader = isHeader;
    }
    public TempNeuroObj(String title, boolean isHeader) {
        this.title = title;
        this.isHeader = isHeader;
    }
    public TempNeuroObj(String title) {
        this.title = title;
    }
}
