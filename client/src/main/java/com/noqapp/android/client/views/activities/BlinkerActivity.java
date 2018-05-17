package com.noqapp.android.client.views.activities;


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

import com.noqapp.android.client.R;


public class BlinkerActivity extends Activity {

    RelativeLayout rl_blinker;
    private TextView tv_close;
    private Thread thread;
    private Vibrator vibrator;
    private boolean stopVibrate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blinker);


        WebView view = (WebView) findViewById(R.id.myWebView);
        view.loadUrl("file:///android_asset/temp.gif");
        view.getSettings().setLoadWithOverviewMode(true);
        view.getSettings().setUseWideViewPort(true);
        rl_blinker = findViewById(R.id.rl_blinker);
        tv_close = findViewById(R.id.tv_close);
        tv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != vibrator)
                    vibrator.cancel();
                if(null != thread)
                    thread.interrupt();
                stopVibrate = true;
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

        if (vibrator.hasVibrator()) {
            final long[] pattern = {0, 1000, 1000, 1000, 1000};
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 5; i++) {
                       if(!stopVibrate) {
                           vibrator.vibrate(pattern, -1);
                           try {
                               Thread.sleep(4000); //the time, the complete pattern needs
                           } catch (Exception e) {
                               e.printStackTrace();
                           }
                       }else{
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
        super.onBackPressed();
        if (null != vibrator)
            vibrator.cancel();
        if(null != thread)
            thread.interrupt();
        stopVibrate = true;
    }
}
