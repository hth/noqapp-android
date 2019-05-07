package com.noqapp.android.common.beans;

import com.noqapp.android.common.model.types.BusinessTypeEnum;

/**
 * Created by chandra on 3/16/18.
 */

public class NotificationBeans {

    private String title;
    private String msg;
    private String status;
    private String notificationCreate;
    private BusinessTypeEnum businessType;
    private String imageUrl;

    public NotificationBeans() {

    }


    public String getTitle() {
        return title;
    }

    public NotificationBeans setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public NotificationBeans setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public NotificationBeans setStatus(String status) {
        this.status = status;
        return this;
    }

    public String getNotificationCreate() {
        return notificationCreate;
    }

    public NotificationBeans setNotificationCreate(String notificationCreate) {
        this.notificationCreate = notificationCreate;
        return this;
    }

    public BusinessTypeEnum getBusinessType() {
        return businessType;
    }

    public NotificationBeans setBusinessType(BusinessTypeEnum businessType) {
        this.businessType = businessType;
        return this;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public NotificationBeans setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("NotificationBeans{");
        sb.append("title='").append(title).append('\'');
        sb.append(", msg='").append(msg).append('\'');
        sb.append(", status='").append(status).append('\'');
        sb.append(", notificationCreate='").append(notificationCreate).append('\'');
        sb.append(", businessType=").append(businessType);
        sb.append(", imageUrl='").append(imageUrl).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
