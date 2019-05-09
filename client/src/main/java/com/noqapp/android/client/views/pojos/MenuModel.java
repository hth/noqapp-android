package com.noqapp.android.client.views.pojos;

public class MenuModel {
    private String title;
    private int icon;
    private boolean hasChildren, isGroup;

    public MenuModel(String title, boolean isGroup, boolean hasChildren, int icon) {
        this.title = title;
        this.icon = icon;
        this.isGroup = isGroup;
        this.hasChildren = hasChildren;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public boolean isHasChildren() {
        return hasChildren;
    }

    public void setHasChildren(boolean hasChildren) {
        this.hasChildren = hasChildren;
    }

    public boolean isGroup() {
        return isGroup;
    }

    public void setGroup(boolean group) {
        isGroup = group;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("MenuModel{");
        sb.append("title='").append(title).append('\'');
        sb.append(", icon=").append(icon);
        sb.append(", hasChildren=").append(hasChildren);
        sb.append(", isGroup=").append(isGroup);
        sb.append('}');
        return sb.toString();
    }
}
