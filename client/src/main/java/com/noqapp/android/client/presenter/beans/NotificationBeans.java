package com.noqapp.android.client.presenter.beans;

/**
 * Created by chandra on 3/16/18.
 */

public class NotificationBeans {

    private String title;
    private String msg;


    public NotificationBeans() {

    }

    public NotificationBeans(String title, String msg) {
        this.title = title;
        this.msg = msg;
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

    @Override
    public String toString() {
        return "NotificationBeans{" +
                "title='" + title + '\'' +
                ", msg='" + msg + '\'' +
                '}';
    }
}
