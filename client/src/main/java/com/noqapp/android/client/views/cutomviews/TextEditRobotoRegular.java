package com.noqapp.android.client.views.cutomviews;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;

import com.noqapp.client.R;

/**
 * User: hitender
 * Date: 5/7/17 12:06 AM
 */

public class TextEditRobotoRegular extends AppCompatEditText {
    private static final String TAG = TextEditRobotoRegular.class.getName();

    private Context context;
    private AttributeSet attrs;
    private int defStyle;

    public TextEditRobotoRegular(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public TextEditRobotoRegular(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.attrs = attrs;
        init();
    }

    public TextEditRobotoRegular(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        this.attrs = attrs;
        this.defStyle = defStyle;
        init();
    }

    private void init() {
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), getContext().getString(R.string.roboto_regular_font));
        this.setTypeface(font);
    }

    @Override
    public void setTypeface(Typeface tf, int style) {
        tf = Typeface.createFromAsset(getContext().getAssets(), getContext().getString(R.string.roboto_regular_font));
        super.setTypeface(tf, style);
    }

    @Override
    public void setTypeface(Typeface tf) {
        tf = Typeface.createFromAsset(getContext().getAssets(), getContext().getString(R.string.roboto_regular_font));
        super.setTypeface(tf);
    }
}
