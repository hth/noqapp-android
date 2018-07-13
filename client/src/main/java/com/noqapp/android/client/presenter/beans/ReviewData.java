package com.noqapp.android.client.presenter.beans;

public class ReviewData {

    private String key;
    private String codeQR;
    private String token;
    private String value;
    private String qUserId;

    public String getKey() {
        return key;
    }

    public ReviewData setKey(String key) {
        this.key = key;
        return this;
    }

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

    public String getValue() {
        return value;
    }

    public ReviewData setValue(String value) {
        this.value = value;
        return this;
    }

    public String getqUserId() {
        return qUserId;
    }

    public ReviewData setqUserId(String qUserId) {
        this.qUserId = qUserId;
        return this;
    }

    @Override
    public String toString() {
        return "ReviewData{" +
                "key='" + key + '\'' +
                ", codeQR='" + codeQR + '\'' +
                ", token='" + token + '\'' +
                ", value='" + value + '\'' +
                ", qUserId='" + qUserId + '\'' +
                '}';
    }
}
