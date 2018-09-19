package com.noqapp.android.common.beans;

public class NavigationBean {

    private int icon;
    private String title;

    public NavigationBean(int icon, String title) {
        this.icon = icon;
        this.title = title;
    }

    public int getIcon() {
        return icon;
    }

    public NavigationBean setIcon(int icon) {
        this.icon = icon;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public NavigationBean setTitle(String title) {
        this.title = title;
        return this;
    }

    @Override
    public String toString() {
        return "NavigationBean{" +
                "icon=" + icon +
                ", title='" + title + '\'' +
                '}';
    }
}
