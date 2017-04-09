package com.noqapp.client.views.cutomviews;


import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;


/**
 * Created by chandra on 4/9/17.
 */

public class TextViewRalewayBold extends  TextView {
    private static final String TAG = "TextView";

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
        String customFont = "fonts/raleway_bold.ttf";
        setCustomFont(ctx, customFont);

    }

    public boolean setCustomFont(Context ctx, String asset) {
        Typeface typeface = null;
        try {
            typeface = Typeface.createFromAsset(ctx.getAssets(), asset);
        } catch (Exception e) {
            Log.e(TAG, "Unable to load typeface: "+e.getMessage());
            return false;
        }
        setTypeface(typeface);
        return true;
    }
}