package com.noqapp.android.common.pojos;

import java.util.ArrayList;
import java.util.List;

public class MenuDrawer {
    private String title;
    private int icon;
    private boolean hasChildren, isGroup;
    private List<MenuDrawer> childList = new ArrayList<>();

    public MenuDrawer(String title, boolean isGroup, boolean hasChildren, int icon) {
        this.title = title;
        this.icon = icon;
        this.isGroup = isGroup;
        this.hasChildren = hasChildren;
    }

    public MenuDrawer(String title, boolean isGroup, boolean hasChildren, int icon, List<MenuDrawer> childList) {
        this.title = title;
        this.icon = icon;
        this.isGroup = isGroup;
        this.hasChildren = hasChildren;
        this.childList = childList;
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

    public List<MenuDrawer> getChildList() {
        return childList;
    }

    public void setChildList(List<MenuDrawer> childList) {
        this.childList = childList;
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("MenuDrawer{");
        sb.append("title='").append(title).append('\'');
        sb.append(", icon=").append(icon);
        sb.append(", hasChildren=").append(hasChildren);
        sb.append(", isGroup=").append(isGroup);
        sb.append(", childList=").append(childList);
        sb.append('}');
        return sb.toString();
    }
}
