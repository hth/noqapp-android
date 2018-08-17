package com.noqapp.android.client.presenter.beans;

public class ReviewData {

    private String codeQR;
    private String token;
    private String qUserId;
    private String isReviewShown;
    private String isSkipped;
    private String gotoCounter;
    private String isBuzzerShow;

    public String getCodeQR() {
        return codeQR;
    }

    public ReviewData setCodeQR(String codeQR) {
        this.codeQR = codeQR;
        return this;
    }

    public String getToken() {
        return token;
    }

    public ReviewData setToken(String token) {
        this.token = token;
        return this;
    }

    public String getqUserId() {
        return qUserId;
    }

    public ReviewData setqUserId(String qUserId) {
        this.qUserId = qUserId;
        return this;
    }

    public String getIsReviewShown() {
        return isReviewShown;
    }

    public ReviewData setIsReviewShown(String isReviewShown) {
        this.isReviewShown = isReviewShown;
        return this;
    }

    public String getIsSkipped() {
        return isSkipped;
    }

    public ReviewData setIsSkipped(String isSkipped) {
        this.isSkipped = isSkipped;
        return this;
    }

    public String getGotoCounter() {
        return gotoCounter;
    }

    public ReviewData setGotoCounter(String gotoCounter) {
        this.gotoCounter = gotoCounter;
        return this;
    }

    public String getIsBuzzerShow() {
        return isBuzzerShow;
    }

    public ReviewData setIsBuzzerShow(String isBuzzerShow) {
        this.isBuzzerShow = isBuzzerShow;
        return this;
    }

    @Override
    public String toString() {
        return "ReviewData{" +
                ", codeQR='" + codeQR + '\'' +
                ", token='" + token + '\'' +
                ", qUserId='" + qUserId + '\'' +
                ", isReviewShown='" + isReviewShown + '\'' +
                ", isSkipped='" + isSkipped + '\'' +
                ", gotoCounter='" + gotoCounter + '\'' +
                ", isBuzzerShow='" + isBuzzerShow + '\'' +
                '}';
    }
}
