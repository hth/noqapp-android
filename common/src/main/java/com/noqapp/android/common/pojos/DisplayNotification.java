package com.noqapp.android.common.pojos;

import com.noqapp.android.common.model.types.BusinessTypeEnum;

/**
 * Created by chandra on 3/16/18.
 */
public class DisplayNotification {

    private String title;
    private String msg;
    private String status;
    private String notificationCreate;
    private BusinessTypeEnum businessType;
    private String imageUrl;
    private Integer sequence;
    private String key;
    private String codeQR;

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
        sb.append('}');
        return sb.toString();
    }
}
