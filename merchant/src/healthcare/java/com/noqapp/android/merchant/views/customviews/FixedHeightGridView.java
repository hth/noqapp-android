package com.noqapp.android.merchant.views.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridView;

public class FixedHeightGridView extends GridView {

    public FixedHeightGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FixedHeightGridView(Context context) {
        super(context);
    }

    public FixedHeightGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                View.MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}