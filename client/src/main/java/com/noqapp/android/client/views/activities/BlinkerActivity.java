package com.noqapp.android.client.views.activities;


import com.noqapp.android.client.R;
import com.noqapp.android.client.utils.AppUtilities;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class BlinkerActivity extends Activity {
    private Thread thread;
    private Vibrator vibrator;
    private boolean stopVibrate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blinker);
        WebView view = findViewById(R.id.myWebView);
        view.loadUrl("file:///android_asset/temp.gif");
        view.getSettings().setLoadWithOverviewMode(true);
        view.getSettings().setUseWideViewPort(true);
        RelativeLayout rl_blinker = findViewById(R.id.rl_blinker);
        TextView tv_close = findViewById(R.id.tv_close);
        tv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopVibrate = true;
                if (null != vibrator)
                    vibrator.cancel();
                if (null != thread)
                    thread.interrupt();
                finish();
            }
        });
        Animation animation = new AlphaAnimation(1, 0); // Change alpha
        // from fully visible to invisible
        animation.setDuration(500); // duration - half a second
        animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
        animation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
        animation.setRepeatMode(Animation.REVERSE); // Reverse animation at the end so the layout will fade back in
        rl_blinker.startAnimation(animation);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        if (AppUtilities.isRelease()) {
            Answers.getInstance().logCustom(new CustomEvent("Buzzer Screen"));
        }

        if (null != vibrator && vibrator.hasVibrator()) {
            final long[] pattern = {0, 1000, 1000, 1000, 1000};
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 5; i++) {
                        if (!stopVibrate) {
                            vibrator.vibrate(pattern, -1);
                            try {
                                Thread.sleep(4000); //the time, the complete pattern needs
                            } catch (InterruptedException e) {
                                if (null != thread)
                                    thread.interrupt();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            return;
                        }
                    }
                }
            };
            thread = new Thread(runnable);
            thread.start();
        }
    }

    @Override
    public void onBackPressed() {
        stopVibrate = true;
        if (null != vibrator)
            vibrator.cancel();
        if (null != thread)
            thread.interrupt();
        super.onBackPressed();
    }
}
