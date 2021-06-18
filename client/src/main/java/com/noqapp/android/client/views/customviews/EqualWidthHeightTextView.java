package com.noqapp.android.client.views.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class EqualWidthHeightTextView extends TextView {

    public EqualWidthHeightTextView(Context context) {
        super(context);
    }

    public EqualWidthHeightTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EqualWidthHeightTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int r = Math.max(getMeasuredWidth(),getMeasuredHeight());
        setMeasuredDimension(r, r);
    }
}
