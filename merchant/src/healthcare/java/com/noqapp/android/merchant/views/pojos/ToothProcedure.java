package com.noqapp.android.merchant.views.pojos;

public class ToothProcedure {
    private String drawable;
    private String drawableLabel;

    public ToothProcedure(String drawable, String drawableLabel) {
        this.drawable = drawable;
        this.drawableLabel = drawableLabel;
    }

    public String getDrawable() {
        return drawable;
    }

    public void setDrawable(String drawable) {
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
