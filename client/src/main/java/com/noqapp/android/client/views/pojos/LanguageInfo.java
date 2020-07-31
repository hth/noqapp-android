package com.noqapp.android.client.views.pojos;

public class LanguageInfo {

    private String languageTitle;
    private String languageCode;
    private int languageDrawable;
    private boolean isLanguageSelected;

    public LanguageInfo(String languageTitle, String languageCode, int languageDrawable, boolean isLanguageSelected) {
        this.languageTitle = languageTitle;
        this.languageCode = languageCode;
        this.languageDrawable = languageDrawable;
        this.isLanguageSelected = isLanguageSelected;
    }

    public String getLanguageTitle() {
        return languageTitle;
    }

    public LanguageInfo setLanguageTitle(String languageTitle) {
        this.languageTitle = languageTitle;
        return this;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public LanguageInfo setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
        return this;
    }

    public int getLanguageDrawable() {
        return languageDrawable;
    }

    public LanguageInfo setLanguageDrawable(int languageDrawable) {
        this.languageDrawable = languageDrawable;
        return this;
    }

    public boolean isLanguageSelected() {
        return isLanguageSelected;
    }

    public LanguageInfo setLanguageSelected(boolean languageSelected) {
        isLanguageSelected = languageSelected;
        return this;
    }

    @Override
    public String toString() {
        return "LanguageInfo{" +
                "languageTitle='" + languageTitle + '\'' +
                ", languageCode='" + languageCode + '\'' +
                ", languageDrawable=" + languageDrawable +
                ", isLanguageSelected=" + isLanguageSelected +
                '}';
    }
}
