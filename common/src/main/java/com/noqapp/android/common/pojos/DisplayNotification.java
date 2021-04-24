package com.noqapp.android.common.pojos;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.noqapp.android.common.model.types.BusinessTypeEnum;

/**
 * Created by chandra on 3/16/18.
 */
@Entity(tableName = "notification")
public class DisplayNotification {

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "key")
    private String key="";
    @ColumnInfo(name = "title")
    private String title;
    @ColumnInfo(name = "msg")
    private String msg;
    @ColumnInfo(name = "status")
    private String status;
    @ColumnInfo(name = "notification_create")
    private String notificationCreate;
    @ColumnInfo(name = "business_type")
    private BusinessTypeEnum businessType;
    @ColumnInfo(name = "image_url")
    private String imageUrl;
    @ColumnInfo(name = "sequence")
    private Integer sequence;
    @ColumnInfo(name = "code_qr")
    private String codeQR;
    @ColumnInfo(name = "type")
    private String type;
    @ColumnInfo(name = "body")
    private String body;
    @ColumnInfo(name = "created_date")
    private String createdDate;

    public DisplayNotification() {

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotificationCreate() {
        return notificationCreate;
    }

    public void setNotificationCreate(String notificationCreate) {
        this.notificationCreate = notificationCreate;
    }

    public BusinessTypeEnum getBusinessType() {
        return businessType;
    }

    public void setBusinessType(BusinessTypeEnum businessType) {
        this.businessType = businessType;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getCodeQR() {
        return codeQR;
    }

    public void setCodeQR(String codeQR) {
        this.codeQR = codeQR;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DisplayNotification{");
        sb.append("title='").append(title).append('\'');
        sb.append(", msg='").append(msg).append('\'');
        sb.append(", status='").append(status).append('\'');
        sb.append(", notificationCreate='").append(notificationCreate).append('\'');
        sb.append(", businessType=").append(businessType);
        sb.append(", imageUrl='").append(imageUrl).append('\'');
        sb.append(", sequence=").append(sequence);
        sb.append(", key='").append(key).append('\'');
        sb.append(", codeQR='").append(codeQR).append('\'');
        sb.append(", type='").append(type).append('\'');
        sb.append(", body='").append(body).append('\'');
        sb.append(", createdDate='").append(createdDate).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
