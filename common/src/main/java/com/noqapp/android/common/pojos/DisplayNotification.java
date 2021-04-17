package com.noqapp.android.common.pojos;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.noqapp.android.common.model.types.BusinessTypeEnum;

/**
 * Created by chandra on 3/16/18.
 */
@Entity(tableName = "notification")
public class DisplayNotification {

    @PrimaryKey
    @ColumnInfo(name = "key")
    private String key;
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

    public DisplayNotification setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public DisplayNotification setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public DisplayNotification setStatus(String status) {
        this.status = status;
        return this;
    }

    public String getNotificationCreate() {
        return notificationCreate;
    }

    public DisplayNotification setNotificationCreate(String notificationCreate) {
        this.notificationCreate = notificationCreate;
        return this;
    }

    public BusinessTypeEnum getBusinessType() {
        return businessType;
    }

    public DisplayNotification setBusinessType(BusinessTypeEnum businessType) {
        this.businessType = businessType;
        return this;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public DisplayNotification setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    public Integer getSequence() {
        return sequence;
    }

    public DisplayNotification setSequence(Integer sequence) {
        this.sequence = sequence;
        return this;
    }

    public String getKey() {
        return key;
    }

    public DisplayNotification setKey(String key) {
        this.key = key;
        return this;
    }

    public String getCodeQR() {
        return codeQR;
    }

    public DisplayNotification setCodeQR(String codeQR) {
        this.codeQR = codeQR;
        return this;
    }

    public String getType() {
        return type;
    }

    public DisplayNotification setType(String type) {
        this.type = type;
        return this;
    }

    public String getBody() {
        return body;
    }

    public DisplayNotification setBody(String body) {
        this.body = body;
        return this;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public DisplayNotification setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
        return this;
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
