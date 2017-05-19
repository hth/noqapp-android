package com.noqapp.android.client.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import com.crashlytics.android.Crashlytics;
import com.noqapp.client.R;

import io.fabric.sdk.android.Fabric;


public class SplashScreen extends AppCompatActivity {

    protected boolean isActive = true;
    protected int splashTime = 2000;
    protected static boolean display = true;
    static SplashScreen splashScreen;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        //   getSupportActionBar().hide();
        SplashHandler mHandler = new SplashHandler();
        setContentView(R.layout.splash);
        splashScreen = this;
        Message msg = new Message();
        msg.what = 0;
        mHandler.sendMessageDelayed(msg, splashTime); // 2 sec delay
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

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            isActive = false;
            display = false;
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }
}