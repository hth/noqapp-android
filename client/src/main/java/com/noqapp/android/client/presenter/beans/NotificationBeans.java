package com.noqapp.android.client.presenter.beans;

/**
 * Created by chandra on 3/16/18.
 */

public class NotificationBeans {

    private String title;
    private String msg;
    private String status;
    private String notificationTime;


    public NotificationBeans() {

    }

    public NotificationBeans(String title, String msg, String status, String notificationTime) {
        this.title = title;
        this.msg = msg;
        this.status = status;
        this.notificationTime =notificationTime;
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

    public String getNotificationTime() {
        return notificationTime;
    }

    public NotificationBeans setNotificationTime(String notificationTime) {
        this.notificationTime = notificationTime;
        return this;
    }

    @Override
    public String toString() {
        return "NotificationBeans{" +
                "title='" + title + '\'' +
                ", msg='" + msg + '\'' +
                ", status='" + status + '\'' +
                ", notificationTime='" + notificationTime + '\'' +
                '}';
    }
}
