package com.noqapp.android.merchant.views.pojos;

public class ToothProcedure {
    private int drawable;
    private String drawableLabel;

    public ToothProcedure(int drawable, String drawableLabel) {
        this.drawable = drawable;
        this.drawableLabel = drawableLabel;
    }

    public int getDrawable() {
        return drawable;
    }

    public void setDrawable(int drawable) {
        this.drawable = drawable;
    }

    public String getDrawableLabel() {
        return drawableLabel;
    }

    public void setDrawableLabel(String drawableLabel) {
        this.drawableLabel = drawableLabel;
    }

    @Override
    public String toString() {
        return "ToothProcedure{" +
                "drawable=" + drawable +
                ", drawableLabel='" + drawableLabel + '\'' +
                '}';
    }
}
