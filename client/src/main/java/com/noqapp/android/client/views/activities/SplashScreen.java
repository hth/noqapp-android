package com.noqapp.android.client.views.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.noqapp.android.client.R;
import com.noqapp.android.client.model.APIConstant;
import com.noqapp.android.client.model.DeviceApiCall;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.client.utils.ErrorResponseHandler;
import com.noqapp.android.common.beans.DeviceRegistered;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.body.DeviceToken;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.presenter.DeviceRegisterPresenter;
import com.noqapp.android.common.utils.NetworkUtil;
import com.noqapp.android.common.utils.PermissionUtils;

import org.apache.commons.lang3.StringUtils;

import java.util.UUID;

import io.fabric.sdk.android.Fabric;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.config.LocationAccuracy;
import io.nlopez.smartlocation.location.config.LocationParams;

///https://blog.xamarin.com/bring-stunning-animations-to-your-apps-with-lottie/
public class SplashScreen extends AppCompatActivity implements DeviceRegisterPresenter {

    static SplashScreen splashScreen;
    private String TAG = SplashScreen.class.getSimpleName();
    private static String fcmToken = "";
    private String APP_PREF = "splashPref";
    private static String deviceId = "";
    private int REQUEST_PERMISSION_SETTING = 23;

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
        callLocationManager();
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
            btn_yes.setOnClickListener((View v) -> {
                mAlertDialog.dismiss();
                finish();
            });
            mAlertDialog.show();
        }
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
            new CustomToast().showToast(this, "Device register error: ");
        }
    }

    private void sendRegistrationToServer(String refreshToken) {
        DeviceToken deviceToken = new DeviceToken(refreshToken, Constants.appVersion());
        //  NoQueueBaseActivity.setFCMToken(refreshToken);
        SharedPreferences sharedpreferences = getApplicationContext().getSharedPreferences(APP_PREF, Context.MODE_PRIVATE);
        deviceId = sharedpreferences.getString(APIConstant.Key.XR_DID, "");
        if (StringUtils.isBlank(deviceId)) {
            deviceId = UUID.randomUUID().toString().toUpperCase();
            //setSharedPreferenceDeviceID(sharedpreferences, deviceId);
            Log.d(TAG, "Created deviceId=" + deviceId);
            sharedpreferences.edit().putString(APIConstant.Key.XR_DID, deviceId).apply();
            //Call this api only once in life time
            DeviceApiCall deviceModel = new DeviceApiCall();
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

    @Override
    public void authenticationFailure() {

    }

    @Override
    public void responseErrorPresenter(int errorCode) {
        new ErrorResponseHandler().processFailureResponseCode(this, errorCode);
    }

    private void callLocationManager() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, new String[]{PermissionUtils.LOCATION_PERMISSION}, PermissionUtils.PERMISSION_REQUEST_LOCATION);
            return;
        }

        long mLocTrackingInterval = 1000 * 60 * 2; // 5 sec
        float trackingDistance = 1;
        LocationAccuracy trackingAccuracy = LocationAccuracy.HIGH;

        LocationParams.Builder builder = new LocationParams.Builder()
                .setAccuracy(trackingAccuracy)
                .setDistance(trackingDistance)
                .setInterval(mLocTrackingInterval);

        SmartLocation.with(this)
                .location()
                .continuous()
                .config(builder.build())
                .start(location -> {
                    if (null != location) {
                        // latitute = location.getLatitude();
                        /// longitute = location.getLongitude();
                        Log.e("Location found: ", "Location detected: Lat- " + location.getLatitude() + " Long- " + location.getLongitude());
                        // getAddress(latitute, longitute);
                        //  updateLocationUI();
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ActivityCompat.shouldShowRequestPermissionRationale(SplashScreen.this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Permission needed")
                    .setMessage("This Action Requires the Location Setting to be enabled. Go to Settings and check the Location Permission inside the Permissions View")
                    .setPositiveButton("Location Settings", (paramDialogInterface, paramInt) -> {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", splashScreen.getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                    })
                    .setNegativeButton("Cancel", (paramDialogInterface, paramInt) -> splashScreen.finish());
            builder.show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{PermissionUtils.LOCATION_PERMISSION}, PermissionUtils.PERMISSION_REQUEST_LOCATION);
        }
    }
}