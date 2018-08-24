package com.noqapp.android.merchant.views.activities;

import com.noqapp.android.merchant.BuildConfig;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.utils.AppUtils;

import com.airbnb.lottie.LottieAnimationView;
import com.crashlytics.android.Crashlytics;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import io.fabric.sdk.android.Fabric;

public class SplashScreen extends AppCompatActivity {

    protected static boolean display = true;
    static SplashScreen splashScreen;
    protected boolean isAactive = true;
    protected int splashTime = BuildConfig.BUILD_TYPE.equals("debug") ? 1000 : 4000;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (!new AppUtils().isTablet(getApplicationContext())) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        //   getSupportActionBar().hide();
        SplashHandler mHandler = new SplashHandler();
        setContentView(R.layout.splash);
        splashScreen = this;
        Message msg = new Message();
        msg.what = 0;
        mHandler.sendMessageDelayed(msg, splashTime); // 4 sec delay
        LottieAnimationView animationView = findViewById(R.id.animation_view);
        animationView.setAnimation("data.json");
        animationView.playAnimation();
        animationView.loop(true);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            isAactive = false;
            display = false;
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    private static class SplashHandler extends Handler {

        // This method is used to handle received messages
        public void handleMessage(Message msg) {
            // switch to identify the message by its code
            switch (msg.what) {
                default:
                case 0:
                    super.handleMessage(msg);
                    if (display) {
                        Intent i = new Intent(splashScreen, LaunchActivity.class);
                        splashScreen.startActivity(i);
                        splashScreen.finish();
                    } else {
                        splashScreen.finish();
                    }
            }
        }
    }
}