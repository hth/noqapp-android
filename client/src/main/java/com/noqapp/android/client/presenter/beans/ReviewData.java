package com.noqapp.android.client.presenter.beans;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "review_data")
public class ReviewData {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "key")
    private Long key = 1L;
    @ColumnInfo(name = "token")
    private String token = "";
    @ColumnInfo(name = "code_qr")
    private String codeQR;
    @ColumnInfo(name = "queue_user_id")
    private String queueUserId;
    @ColumnInfo(name = "is_review_shown")
    private String isReviewShown;
    @ColumnInfo(name = "is_skipped")
    private String isSkipped;
    @ColumnInfo(name = "go_to_counter")
    private String gotoCounter;
    @ColumnInfo(name = "is_buzzer_show")
    private String isBuzzerShow;
    @ColumnInfo(name = "review_type")
    private String type;

    @NonNull
    public String getToken() {
        return token;
    }

    public void setToken(@NonNull String token) {
        this.token = token;
    }

    public String getCodeQR() {
        return codeQR;
    }

    public void setCodeQR(String codeQR) {
        this.codeQR = codeQR;
    }

    public String getQueueUserId() {
        return queueUserId;
    }

    public void setQueueUserId(String queueUserId) {
        this.queueUserId = queueUserId;
    }

    public String getIsReviewShown() {
        return isReviewShown;
    }

    public void setIsReviewShown(String isReviewShown) {
        this.isReviewShown = isReviewShown;
    }

    public String getIsSkipped() {
        return isSkipped;
    }

    public void setIsSkipped(String isSkipped) {
        this.isSkipped = isSkipped;
    }

    public String getGotoCounter() {
        return gotoCounter;
    }

    public void setGotoCounter(String gotoCounter) {
        this.gotoCounter = gotoCounter;
    }

    public String getIsBuzzerShow() {
        return isBuzzerShow;
    }

    public void setIsBuzzerShow(String isBuzzerShow) {
        this.isBuzzerShow = isBuzzerShow;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getKey() {
        return key;
    }

    public void setKey(Long key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "ReviewData{" +
                ", codeQR='" + codeQR + '\'' +
                ", token='" + token + '\'' +
                ", qUserId='" + queueUserId + '\'' +
                ", isReviewShown='" + isReviewShown + '\'' +
                ", isSkipped='" + isSkipped + '\'' +
                ", gotoCounter='" + gotoCounter + '\'' +
                ", isBuzzerShow='" + isBuzzerShow + '\'' +
                ", type='" + type + '\'' +
                ", key='" + key + '\'' +
                '}';
    }
}
