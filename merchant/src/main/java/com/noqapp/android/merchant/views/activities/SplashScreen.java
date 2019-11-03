package com.noqapp.android.merchant.views.activities;

import com.noqapp.android.common.beans.DeviceRegistered;
import com.noqapp.android.common.beans.body.DeviceToken;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.presenter.DeviceRegisterPresenter;
import com.noqapp.android.common.utils.NetworkUtil;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.APIConstant;
import com.noqapp.android.merchant.model.DeviceApiCalls;
import com.noqapp.android.merchant.utils.Constants;

import com.google.firebase.iid.FirebaseInstanceId;

import com.airbnb.lottie.LottieAnimationView;
import com.crashlytics.android.Crashlytics;

import org.apache.commons.lang3.StringUtils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import io.fabric.sdk.android.Fabric;

import java.util.UUID;

public class SplashScreen extends BaseActivity implements DeviceRegisterPresenter {

    static SplashScreen splashScreen;
    private static String fcmToken = "";
    private String APP_PREF = "splashPref";
    private static String deviceId = "";
    private String TAG = SplashScreen.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setScreenOrientation();
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        //   getSupportActionBar().hide();
        setContentView(R.layout.splash);
        splashScreen = this;
        LottieAnimationView animationView = findViewById(R.id.animation_view);
        animationView.setAnimation("data.json");
        animationView.playAnimation();
        animationView.setRepeatCount(10);

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this, instanceIdResult -> {
            String newToken = instanceIdResult.getToken();
            Log.e("newToken", newToken);
            fcmToken = newToken;
            Log.d(BaseLaunchActivity.class.getSimpleName(), "FCM Token=" + fcmToken);
            sendRegistrationToServer(fcmToken);
        });

        if (StringUtils.isBlank(fcmToken) && new NetworkUtil(this).isNotOnline()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = LayoutInflater.from(this);
            builder.setTitle(null);
            View customDialogView = inflater.inflate(R.layout.dialog_general, null, false);
            TextView tvTitle = customDialogView.findViewById(R.id.tvtitle);
            TextView tv_msg = customDialogView.findViewById(R.id.tv_msg);
            tvTitle.setText(getString(R.string.networkerror));
            tv_msg.setText(getString(R.string.offline));
            builder.setView(customDialogView);
            final AlertDialog mAlertDialog = builder.create();
            mAlertDialog.setCanceledOnTouchOutside(false);
            Button btn_yes = customDialogView.findViewById(R.id.btn_yes);
            btn_yes.setOnClickListener(v -> {
                mAlertDialog.dismiss();
                finish();
            });
            mAlertDialog.show();
            Log.w(TAG, "No network found");
        }
    }

    private void sendRegistrationToServer(String refreshToken) {
        if (new NetworkUtil(this).isOnline()) {
            DeviceToken deviceToken = new DeviceToken(refreshToken, Constants.appVersion());
            SharedPreferences sharedpreferences = getApplicationContext().getSharedPreferences(APP_PREF, Context.MODE_PRIVATE);
            deviceId = sharedpreferences.getString(APIConstant.Key.XR_DID, "");
            if (StringUtils.isBlank(deviceId)) {
                deviceId = UUID.randomUUID().toString().toUpperCase();
                Log.d(TAG, "Created deviceId=" + deviceId);
                sharedpreferences.edit().putString(APIConstant.Key.XR_DID, deviceId).apply();
                //Call this api only once in life time
                DeviceApiCalls deviceApiCalls = new DeviceApiCalls();
                deviceApiCalls.setDeviceRegisterPresenter(this);
                deviceApiCalls.register(deviceId, deviceToken);
            } else {
                Log.e("Launch", "launching from sendRegistrationToServer");
                Log.d(TAG, "Exist deviceId=" + deviceId);
                Intent i = new Intent(splashScreen, LaunchActivity.class);
                i.putExtra("fcmToken", fcmToken);
                i.putExtra("deviceId", deviceId);
                splashScreen.startActivity(i);
                splashScreen.finish();
            }
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = LayoutInflater.from(this);
            builder.setTitle(null);
            View customDialogView = inflater.inflate(R.layout.dialog_general, null, false);
            TextView tvTitle = customDialogView.findViewById(R.id.tvtitle);
            TextView tv_msg = customDialogView.findViewById(R.id.tv_msg);
            tvTitle.setText(getString(R.string.networkerror));
            tv_msg.setText(getString(R.string.offline));
            builder.setView(customDialogView);
            final AlertDialog mAlertDialog = builder.create();
            mAlertDialog.setCanceledOnTouchOutside(false);
            Button btn_yes = customDialogView.findViewById(R.id.btn_yes);
            btn_yes.setOnClickListener(v -> {
                mAlertDialog.dismiss();
                finish();
            });
            mAlertDialog.show();
            Log.w(TAG, "No network found");
        }
    }

    @Override
    public void deviceRegisterError() {
    }

    @Override
    public void authenticationFailure() {
    }

    @Override
    public void deviceRegisterResponse(DeviceRegistered deviceRegistered) {
        if (deviceRegistered.getRegistered() == 1) {
            Log.e("Launch", "launching from deviceRegisterResponse");
            Intent i = new Intent(splashScreen, LaunchActivity.class);
            i.putExtra("fcmToken", fcmToken);
            i.putExtra("deviceId", deviceId);
            splashScreen.startActivity(i);
            splashScreen.finish();
        } else {
            Log.e("Device register error: ", deviceRegistered.toString());
            new CustomToast().showToast(this, "Device register error: ");
        }
    }
}