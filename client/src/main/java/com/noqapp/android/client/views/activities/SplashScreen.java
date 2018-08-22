package com.noqapp.android.client.views.activities;

import com.noqapp.android.client.R;
import com.noqapp.android.client.model.DeviceModel;
import com.noqapp.android.client.views.interfaces.DeviceRegisterPresenter;
import com.noqapp.android.common.beans.DeviceRegistered;
import com.noqapp.android.common.beans.body.DeviceToken;
import com.noqapp.android.common.utils.NetworkUtil;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import com.airbnb.lottie.LottieAnimationView;
import com.crashlytics.android.Crashlytics;

import org.apache.commons.lang3.StringUtils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import io.fabric.sdk.android.Fabric;

import java.util.UUID;

///https://blog.xamarin.com/bring-stunning-animations-to-your-apps-with-lottie/
public class SplashScreen extends AppCompatActivity implements DeviceRegisterPresenter{

    protected static boolean display = true;
    static SplashScreen splashScreen;
    protected boolean isActive = true;
    protected int splashTime = 40000;
    private String TAG = SplashScreen.class.getSimpleName();
    private String fcmToken = "";
    private String APP_PREF = "splashPref";
    private String deviceId = "";
    //BuildConfig.BUILD_TYPE.equals("debug") ? 1000 : 4000;

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
        mHandler.sendMessageDelayed(msg, splashTime); // 4 sec delay
        LottieAnimationView animationView = findViewById(R.id.animation_view);
        animationView.setAnimation("data.json");
        animationView.playAnimation();
        animationView.loop(true);
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(SplashScreen.this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String newToken = instanceIdResult.getToken();
                Log.e("newToken", newToken);
                fcmToken = newToken;
                Log.d(TAG, "FCM Token=" + fcmToken);
                sendRegistrationToServer(fcmToken);
            }
        });
        if(fcmToken.equals("") && !new NetworkUtil(this).isOnline()){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = LayoutInflater.from(this);
            builder.setTitle(null);
            View customDialogView = inflater.inflate(R.layout.dialog_general, null, false);
            TextView tvtitle = customDialogView.findViewById(R.id.tvtitle);
            TextView tv_msg = customDialogView.findViewById(R.id.tv_msg);
            tvtitle.setText(getString(R.string.networkerror));
            tv_msg.setText(getString(R.string.offline));
            builder.setView(customDialogView);
            final AlertDialog mAlertDialog = builder.create();
            mAlertDialog.setCanceledOnTouchOutside(false);
            Button btn_yes = customDialogView.findViewById(R.id.btn_yes);

            btn_yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAlertDialog.dismiss();
                    finish();
                }
            });
            mAlertDialog.show();
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

    @Override
    public void deviceRegisterError() {

    }

    @Override
    public void deviceRegisterResponse(DeviceRegistered deviceRegistered) {
        if(deviceRegistered.getRegistered() == 1) {
            Intent i = new Intent(splashScreen, LaunchActivity.class);
            i.putExtra("fcmToken", fcmToken);
            i.putExtra("deviceId", deviceId);
            splashScreen.startActivity(i);
            splashScreen.finish();
        }else{
            Log.e("Device register error: ",deviceRegistered.toString());
        }
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
    private void sendRegistrationToServer(String refreshToken) {
        DeviceToken deviceToken = new DeviceToken(refreshToken);
      //  NoQueueBaseActivity.setFCMToken(refreshToken);
        SharedPreferences sharedpreferences = getApplicationContext().getSharedPreferences(
                APP_PREF, Context.MODE_PRIVATE);
        deviceId = sharedpreferences.getString(NoQueueBaseActivity.XR_DID, "");
        if (StringUtils.isBlank(deviceId)) {
            deviceId = UUID.randomUUID().toString().toUpperCase();
            //setSharedPreferenceDeviceID(sharedpreferences, deviceId);
            Log.d(TAG, "Created deviceId=" + deviceId);
            sharedpreferences.edit().putString(NoQueueBaseActivity.XR_DID,deviceId).apply();
            //Call this api only once in life time
            DeviceModel deviceModel = new DeviceModel();
            deviceModel.setDeviceRegisterPresenter(this);
            deviceModel.register(deviceId, deviceToken);
        } else {
            Log.d(TAG, "Exist deviceId=" + deviceId);
            Intent i = new Intent(splashScreen, LaunchActivity.class);
            i.putExtra("fcmToken",fcmToken);
            i.putExtra("deviceId",deviceId);
            splashScreen.startActivity(i);
            splashScreen.finish();
        }
    }
}