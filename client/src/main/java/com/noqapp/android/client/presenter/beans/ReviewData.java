package com.noqapp.android.client.presenter.beans;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "review_data")
public class ReviewData {

    @PrimaryKey
    @ColumnInfo(name = "token")
    private String token;
    @ColumnInfo(name = "code_qr")
    private String codeQR;
    @ColumnInfo(name = "q_user_id")
    private String qUserId;
    @ColumnInfo(name = "is_review_shown")
    private String isReviewShown;
    @ColumnInfo(name = "is_skipped")
    private String isSkipped;
    @ColumnInfo(name = "go_to_counter")
    private String gotoCounter;
    @ColumnInfo(name = "is_buzzer_show")
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
