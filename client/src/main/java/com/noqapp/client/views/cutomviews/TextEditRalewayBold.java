package com.noqapp.client.views.cutomviews;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;

import com.noqapp.client.R;

/**
 * User: hitender
 * Date: 5/7/17 12:05 AM
 */
public class TextEditRalewayBold extends AppCompatEditText {
    private static final String TAG = TextEditRobotoRegular.class.getName();

    private Context context;
    private AttributeSet attrs;
    private int defStyle;

    public TextEditRalewayBold(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public TextEditRalewayBold(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.attrs = attrs;
        init();
    }

    public TextEditRalewayBold(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        this.attrs = attrs;
        this.defStyle = defStyle;
        init();
    }

    private void init() {
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), getContext().getString(R.string.raleway_bold_font));
        this.setTypeface(font);
    }

    @Override
    public void setTypeface(Typeface tf, int style) {
        tf = Typeface.createFromAsset(getContext().getAssets(), getContext().getString(R.string.raleway_bold_font));
        super.setTypeface(tf, style);
    }

    @Override
    public void setTypeface(Typeface tf) {
        tf = Typeface.createFromAsset(getContext().getAssets(), getContext().getString(R.string.raleway_bold_font));
        super.setTypeface(tf);
    }
}