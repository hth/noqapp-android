package com.noqapp.android.client.views.cutomviews;


import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.util.Log;

import com.noqapp.android.client.R;

/**
 * Created by chandra on 4/9/17.
 */
public class ButtonRalewayRegular extends AppCompatButton {
    private static final String TAG = ButtonRalewayRegular.class.getName();

    public ButtonRalewayRegular(Context context) {
        super(context);
    }

    public ButtonRalewayRegular(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomFont(context, attrs);
    }

    public ButtonRalewayRegular(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setCustomFont(context, attrs);
    }

    private void setCustomFont(Context ctx, AttributeSet attrs) {
        setCustomFont(ctx, ctx.getString(R.string.raleway_regular_font));
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