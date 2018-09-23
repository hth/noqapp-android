package com.noqapp.android.client.views.activities;

import com.noqapp.android.client.R;
import com.noqapp.android.client.model.DeviceModel;
import com.noqapp.android.client.presenter.DeviceRegisterPresenter;
import com.noqapp.android.client.utils.ErrorResponseHandler;
import com.noqapp.android.common.beans.DeviceRegistered;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
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
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import io.fabric.sdk.android.Fabric;

import java.util.UUID;

///https://blog.xamarin.com/bring-stunning-animations-to-your-apps-with-lottie/
public class SplashScreen extends AppCompatActivity implements DeviceRegisterPresenter {

    protected static boolean display = true;
    static SplashScreen splashScreen;
    protected boolean isActive = true;

    private String TAG = SplashScreen.class.getSimpleName();
    private static String fcmToken = "";
    private String APP_PREF = "splashPref";
    private static String deviceId = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        //   getSupportActionBar().hide();
        setContentView(R.layout.splash);
        splashScreen = this;
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
        if (fcmToken.equals("") && !new NetworkUtil(this).isOnline()) {
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
    public void responseErrorPresenter(ErrorEncounteredJson eej) {
        new ErrorResponseHandler().processError(this, eej);
    }

    @Override
    public void deviceRegisterResponse(DeviceRegistered deviceRegistered) {
        if (deviceRegistered.getRegistered() == 1) {
            Log.e("Launch", "launching from deviceRegisterResponse");
            Intent i = new Intent(splashScreen, LaunchActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.putExtra("fcmToken", fcmToken);
            i.putExtra("deviceId", deviceId);
            splashScreen.startActivity(i);
            splashScreen.finish();
        } else {
            Log.e("Device register error: ", deviceRegistered.toString());
            Toast.makeText(this, "Device register error: ", Toast.LENGTH_LONG).show();
        }
    }


    private void sendRegistrationToServer(String refreshToken) {
        DeviceToken deviceToken = new DeviceToken(refreshToken);
        //  NoQueueBaseActivity.setFCMToken(refreshToken);
        SharedPreferences sharedpreferences = getApplicationContext().getSharedPreferences(APP_PREF, Context.MODE_PRIVATE);
        deviceId = sharedpreferences.getString(NoQueueBaseActivity.XR_DID, "");
        if (StringUtils.isBlank(deviceId)) {
            deviceId = UUID.randomUUID().toString().toUpperCase();
            //setSharedPreferenceDeviceID(sharedpreferences, deviceId);
            Log.d(TAG, "Created deviceId=" + deviceId);
            sharedpreferences.edit().putString(NoQueueBaseActivity.XR_DID, deviceId).apply();
            //Call this api only once in life time
            DeviceModel deviceModel = new DeviceModel();
            deviceModel.setDeviceRegisterPresenter(this);
            deviceModel.register(deviceId, deviceToken);
        } else {
            Log.e("Launch", "launching from sendRegistrationToServer");
            Log.d(TAG, "Exist deviceId=" + deviceId);
            Intent i = new Intent(splashScreen, LaunchActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.putExtra("fcmToken", fcmToken);
            i.putExtra("deviceId", deviceId);
            splashScreen.startActivity(i);
            splashScreen.finish();
        }
    }
}