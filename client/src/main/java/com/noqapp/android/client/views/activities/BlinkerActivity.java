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

    RelativeLayout rl_blinker1;
    private TextView tv_close;
    private Thread thread;
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blinker);


        WebView view = (WebView) findViewById(R.id.myWebView);
        view.loadUrl("file:///android_asset/temp.gif");
        view.getSettings().setLoadWithOverviewMode(true);
        view.getSettings().setUseWideViewPort(true);
        rl_blinker1 = findViewById(R.id.rl_blinker1);
        tv_close = findViewById(R.id.tv_close);
        tv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != vibrator)
                    vibrator.cancel();
                finish();
            }
        });
        Animation animation = new AlphaAnimation(1, 0); // Change alpha
        // from fully visible to invisible
        animation.setDuration(500); // duration - half a second
        animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
        animation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
        animation.setRepeatMode(Animation.REVERSE); // Reverse animation at the end so the layout will fade back in
        rl_blinker1.startAnimation(animation);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            v.vibrate(VibrationEffect.createOneShot(500,VibrationEffect.DEFAULT_AMPLITUDE));
//        }else{
//            //deprecated in API 26
//            v.vibrate(500);
//        }


        if (vibrator.hasVibrator()) {
            final long[] pattern = {0, 1000, 1000, 1000, 1000};
            new Thread() {
                @Override
                public void run() {
                    for (int i = 0; i < 5; i++) { //repeat the pattern 1 times
                        vibrator.vibrate(pattern, -1);
                        try {
                            Thread.sleep(4000); //the time, the complete pattern needs
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }.start();


        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != vibrator)
            vibrator.cancel();
    }
}
