package com.noqapp.android.merchant.views.pojos;

import java.util.ArrayList;
import java.util.List;

public class ToothInfo {
    private ToothProcedure toothFrontView;
    private ToothProcedure toothTopView;
    private ToothProcedure toothDefaultFrontView;
    private ToothProcedure toothDefaultTopView;
    private int toothNumber;
    private List<ToothProcedure> topViewDrawables = new ArrayList<>();
    private List<ToothProcedure> frontViewDrawables = new ArrayList<>();
    private boolean isUpdated = false;

    public ToothInfo() {
        topViewDrawables.clear();
        frontViewDrawables.clear();
    }

    public ToothProcedure getToothFrontView() {
        return toothFrontView;
    }

    public void setToothFrontView(ToothProcedure toothFrontView) {
        this.toothFrontView = toothFrontView;
    }

    public ToothProcedure getToothTopView() {
        return toothTopView;
    }

    public void setToothTopView(ToothProcedure toothTopView) {
        this.toothTopView = toothTopView;
    }

    public ToothProcedure getToothDefaultFrontView() {
        return toothDefaultFrontView;
    }

    public void setToothDefaultFrontView(ToothProcedure toothDefaultFrontView) {
        this.toothDefaultFrontView = toothDefaultFrontView;
    }

    public ToothProcedure getToothDefaultTopView() {
        return toothDefaultTopView;
    }

    public void setToothDefaultTopView(ToothProcedure toothDefaultTopView) {
        this.toothDefaultTopView = toothDefaultTopView;
    }

    public int getToothNumber() {
        return toothNumber;
    }

    public void setToothNumber(int toothNumber) {
        this.toothNumber = toothNumber;
    }

    public List<ToothProcedure> getTopViewDrawables() {
        return topViewDrawables;
    }

    public void setTopViewDrawables(List<ToothProcedure> topViewDrawables) {
        this.topViewDrawables = topViewDrawables;
    }

    public List<ToothProcedure> getFrontViewDrawables() {
        return frontViewDrawables;
    }

    public void setFrontViewDrawables(List<ToothProcedure> frontViewDrawables) {
        this.frontViewDrawables = frontViewDrawables;
    }

    public boolean isUpdated() {
        return isUpdated;
    }

    public void setUpdated(boolean updated) {
        isUpdated = updated;
    }

    @Override
    public String toString() {
        return "ToothInfo{" +
                "toothFrontView=" + toothFrontView +
                ", toothTopView=" + toothTopView +
                ", toothDefaultFrontView=" + toothDefaultFrontView +
                ", toothDefaultTopView=" + toothDefaultTopView +
                ", toothNumber=" + toothNumber +
                ", topViewDrawables=" + topViewDrawables +
                ", frontViewDrawables=" + frontViewDrawables +
                ", isUpdated=" + isUpdated +
                '}';
    }
}
