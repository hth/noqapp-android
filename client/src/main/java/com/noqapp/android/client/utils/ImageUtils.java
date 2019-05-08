package com.noqapp.android.client.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.noqapp.android.client.R;

public class ImageUtils {

    public static Drawable getThumbPlaceholder(Context context) {
        return context.getResources().getDrawable(R.drawable.thumb_placeholder);
    }

    public static Drawable getBannerPlaceholder(Context context) {
        return context.getResources().getDrawable(R.drawable.header_placeholder);
    }

    public static Drawable getThumbErrorPlaceholder(Context context) {
        return context.getResources().getDrawable(R.drawable.thumb_error);
    }

    public static Drawable getBannerErrorPlaceholder(Context context) {
        return context.getResources().getDrawable(R.drawable.header_error);
    }

    public static Drawable getProfilePlaceholder(Context context) {
        return context.getResources().getDrawable(R.drawable.profile_theme);
    }

    public static Drawable getProfileErrorPlaceholder(Context context) {
        return context.getResources().getDrawable(R.drawable.profile_theme);
    }

    public static int getBannerPlaceholder() {
        return R.drawable.header_placeholder;
    }

    public static int getThumbPlaceholder() {
        return R.drawable.thumb_placeholder;
    }

    public static int getProfilePlaceholder() {
        return R.drawable.profile_theme;
    }
}
