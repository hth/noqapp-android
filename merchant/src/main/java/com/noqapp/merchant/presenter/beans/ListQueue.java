package com.noqapp.merchant.presenter.beans;


import com.fasterxml.jackson.annotation.JsonProperty;

public class ListQueue {

    @JsonProperty("c")
    private String codeQR;

    @JsonProperty("d")
    private String displayName;

    @JsonProperty("a")
    private String active;

    @JsonProperty("s")
    private int servingNumber;

    @JsonProperty("t")
    private int token;

    public ListQueue(String codeQR, String displayName, String active, int servingNumber, int token) {
        this.codeQR = codeQR;
        this.displayName = displayName;
        this.active = active;
        this.servingNumber = servingNumber;
        this.token = token;
    }

    public String getCodeQR() {
        return codeQR;
    }

    public void setCodeQR(String codeQR) {
        this.codeQR = codeQR;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }



    public int getServingNumber() {
        return servingNumber;
    }

    public void setServingNumber(int servingNumber) {
        this.servingNumber = servingNumber;
    }

    public int getToken() {
        return token;
    }

    public void setToken(int token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "ListQueue{" +
                "codeQR='" + codeQR + '\'' +
                ", displayName='" + displayName + '\'' +
                ", active='" + active + '\'' +
                ", servingNumber=" + servingNumber +
                ", token=" + token +
                '}';
    }
}