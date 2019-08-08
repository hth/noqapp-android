package com.noqapp.android.merchant.views.pojos;

import java.util.ArrayList;
import java.util.List;

public class ToothInfo {
    private int toothFrontView;
    private int toothTopView;
    private int toothNumber;
    private List<Integer> topViewDrawables = new ArrayList<>();
    private List<Integer> frontViewDrawables = new ArrayList<>();

    public ToothInfo() {
        topViewDrawables.clear();
        frontViewDrawables.clear();
    }

    public int getToothFrontView() {
        return toothFrontView;
    }

    public ToothInfo setToothFrontView(int toothFrontView) {
        this.toothFrontView = toothFrontView;
        return this;
    }

    public int getToothTopView() {
        return toothTopView;
    }

    public ToothInfo setToothTopView(int toothTopView) {
        this.toothTopView = toothTopView;
        return this;
    }

    public int getToothNumber() {
        return toothNumber;
    }

    public ToothInfo setToothNumber(int toothNumber) {
        this.toothNumber = toothNumber;
        return this;
    }

    public List<Integer> getTopViewDrawables() {
        return topViewDrawables;
    }

    public ToothInfo setTopViewDrawables(List<Integer> topViewDrawables) {
        this.topViewDrawables = topViewDrawables;
        return this;
    }

    public List<Integer> getFrontViewDrawables() {
        return frontViewDrawables;
    }

    public ToothInfo setFrontViewDrawables(List<Integer> frontViewDrawables) {
        this.frontViewDrawables = frontViewDrawables;
        return this;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ToothInfo{");
        sb.append("toothFrontView=").append(toothFrontView);
        sb.append(", toothTopView=").append(toothTopView);
        sb.append(", toothNumber=").append(toothNumber);
        sb.append(", topViewDrawables=").append(topViewDrawables);
        sb.append(", frontViewDrawables=").append(frontViewDrawables);
        sb.append('}');
        return sb.toString();
    }
}
