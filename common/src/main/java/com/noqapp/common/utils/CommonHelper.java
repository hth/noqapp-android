package com.noqapp.common.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CommonHelper {

    private static final SimpleDateFormat SDF_DOB_FROM_UI = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
    private static final SimpleDateFormat SDF_DOB = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    public static String convertDOBToValidFormat(String dob) {
        try {
            Date date = SDF_DOB_FROM_UI.parse(dob);
            return SDF_DOB.format(date);
        } catch (ParseException e) {
            Log.e("Date error", "Error parsing DOB={}" + e.getLocalizedMessage(), e);
            return "";
        }
    }

    public static void openPlayStore(Context context) {
        final String appPackageName = context.getPackageName(); // getPackageName() from Context or Activity object
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    public static void shareTheApp(Context context) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                "Hey check out my app at: https://play.google.com/store/apps/details?id=" + context.getPackageName());
        sendIntent.setType("text/plain");
        context.startActivity(sendIntent);
    }


    public void hideKeyBoard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    /*
     * Method add to hide the dropdown while setting the
     * AutoCompleteTextView
     */
    public static void setAutoCompleteText(AutoCompleteTextView autoCompleteTextView, String text) {
        autoCompleteTextView.setFocusable(false);
        autoCompleteTextView.setFocusableInTouchMode(false);
        autoCompleteTextView.setText(text);
        autoCompleteTextView.setSelection(text.length()); // to make the cursor at end of the text
        autoCompleteTextView.setFocusable(true);
        autoCompleteTextView.setFocusableInTouchMode(true);
        autoCompleteTextView.dismissDropDown();
    }
}
