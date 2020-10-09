package com.noqapp.android.client.views.pojos;

import java.io.Serializable;

public class KioskModeInfo implements Serializable {
    private boolean isKioskModeEnable;
    private boolean isLevelUp;
    private boolean isFeedbackScreen;
    private String kioskCodeQR;
    private String bizNameId;
    private String bizName;

    public boolean isKioskModeEnable() {
        return isKioskModeEnable;
    }

    public void setKioskModeEnable(boolean kioskModeEnable) {
        isKioskModeEnable = kioskModeEnable;
    }

    public boolean isLevelUp() {
        return isLevelUp;
    }

    public void setLevelUp(boolean levelUp) {
        isLevelUp = levelUp;
    }

    public boolean isFeedbackScreen() {
        return isFeedbackScreen;
    }

    public void setFeedbackScreen(boolean feedbackScreen) {
        isFeedbackScreen = feedbackScreen;
    }

    public String getKioskCodeQR() {
        return kioskCodeQR;
    }

    public void setKioskCodeQR(String kioskCodeQR) {
        this.kioskCodeQR = kioskCodeQR;
    }

    public String getBizNameId() {
        return bizNameId;
    }

    public void setBizNameId(String bizNameId) {
        this.bizNameId = bizNameId;
    }

    public String getBizName() {
        return bizName;
    }

    public void setBizName(String bizName) {
        this.bizName = bizName;
    }

    @Override
    public String toString() {
        return "KioskModeInfo{" +
            "isKioskModeEnable=" + isKioskModeEnable +
            ", isLevelUp=" + isLevelUp +
            ", isFeedbackScreen=" + isFeedbackScreen +
            ", kioskCodeQR='" + kioskCodeQR + '\'' +
            ", bizNameId='" + bizNameId + '\'' +
            ", bizName='" + bizName + '\'' +
            '}';
    }
}
