package com.noqapp.android.client.views.customviews;


import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.Log;

import com.noqapp.android.client.R;

/**
 * Created by chandra on 4/9/17.
 */
public class TextViewRalewayBold extends AppCompatTextView {
    private static final String TAG = TextViewRalewayBold.class.getName();

    public TextViewRalewayBold(Context context) {
        super(context);
    }

    public TextViewRalewayBold(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomFont(context, attrs);
    }

    public TextViewRalewayBold(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setCustomFont(context, attrs);
    }

    private void setCustomFont(Context ctx, AttributeSet attrs) {
        setCustomFont(ctx, ctx.getString(R.string.raleway_bold_font));
    }

    public boolean setCustomFont(Context ctx, String asset) {
        Typeface typeface;
        try {
            typeface = Typeface.createFromAsset(ctx.getAssets(), asset);
        } catch (Exception e) {
            Log.e(TAG, "Unable to load typeface: " + e.getMessage());
            return false;
        }
        setTypeface(typeface);
        return true;
    }
}