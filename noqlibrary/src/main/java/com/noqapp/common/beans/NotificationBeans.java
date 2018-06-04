package com.noqapp.common.beans;

/**
 * Created by chandra on 3/16/18.
 */

public class NotificationBeans {

    private String title;
    private String msg;
    private String status;
    private String notificationCreate;


    public NotificationBeans() {

    }

    public NotificationBeans(String title, String msg, String status, String notificationCreate) {
        this.title = title;
        this.msg = msg;
        this.status = status;
        this.notificationCreate = notificationCreate;
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

    @Override
    public String toString() {
        return "NotificationBeans{" +
                "title='" + title + '\'' +
                ", msg='" + msg + '\'' +
                ", status='" + status + '\'' +
                ", notificationCreate='" + notificationCreate + '\'' +
                '}';
    }
}
