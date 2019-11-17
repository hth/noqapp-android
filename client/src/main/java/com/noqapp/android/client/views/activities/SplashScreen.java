package com.noqapp.android.client.views.activities;

import com.noqapp.android.client.R;
import com.noqapp.android.client.model.APIConstant;
import com.noqapp.android.client.model.DeviceApiCall;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.client.utils.ErrorResponseHandler;
import com.noqapp.android.client.utils.GPSTracker;
import com.noqapp.android.common.beans.DeviceRegistered;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.body.DeviceToken;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.presenter.DeviceRegisterPresenter;
import com.noqapp.android.common.utils.NetworkUtil;
import com.noqapp.android.common.utils.PermissionUtils;

import com.google.firebase.iid.FirebaseInstanceId;

import com.airbnb.lottie.LottieAnimationView;
import com.crashlytics.android.Crashlytics;

import org.apache.commons.lang3.StringUtils;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import io.fabric.sdk.android.Fabric;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.config.LocationAccuracy;
import io.nlopez.smartlocation.location.config.LocationParams;

import java.util.UUID;

///https://blog.xamarin.com/bring-stunning-animations-to-your-apps-with-lottie/
public class SplashScreen extends AppCompatActivity implements DeviceRegisterPresenter {

    static SplashScreen splashScreen;
    private String TAG = SplashScreen.class.getSimpleName();
    private static String fcmToken = "";
    private String APP_PREF = "splashPref";
    private static String deviceId = "";
    private final int REQUEST_PERMISSION_SETTING = 23;
    public final int GPS_ENABLE_REQUEST = 24;
    private Location location;
    private GPSTracker gpsTracker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.splash);
        splashScreen = this;
        LottieAnimationView animationView = findViewById(R.id.animation_view);
        animationView.setAnimation("data.json");
        animationView.playAnimation();
        animationView.setRepeatCount(10);

        gpsTracker = new GPSTracker(this, null);
        if (gpsTracker.isLocationEnabled()) {
            callLocationManager();
        } else {
            final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("Enable Location")
                    .setMessage("Location is disabled, in order to use the application you need to enable location in your device")
                    .setPositiveButton("Location Settings", (paramDialogInterface, paramInt) -> {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(myIntent, GPS_ENABLE_REQUEST);
                    })
                    .setNegativeButton("Cancel", (paramDialogInterface, paramInt) -> finish());
            dialog.show();
        }

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(SplashScreen.this, instanceIdResult -> {
            String newToken = instanceIdResult.getToken();
            Log.e("newToken", newToken);
            fcmToken = newToken;
            Log.d(TAG, "FCM Token=" + fcmToken);
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
            btn_yes.setOnClickListener((View v) -> {
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
    public void responseErrorPresenter(ErrorEncounteredJson eej) {
        new ErrorResponseHandler().processError(this, eej);
    }

    @Override
    public void deviceRegisterResponse(DeviceRegistered deviceRegistered) {
        if (deviceRegistered.getRegistered() == 1) {
            Log.e("Launch", "launching from deviceRegisterResponse");
            callLaunchScreen();
        } else {
            Log.e("Device register error: ", deviceRegistered.toString());
            new CustomToast().showToast(this, "Device register error: ");
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
                DeviceApiCall deviceModel = new DeviceApiCall();
                deviceModel.setDeviceRegisterPresenter(this);
                deviceModel.register(deviceId, deviceToken);
            } else {
                Log.e("Launch", "launching from sendRegistrationToServer");
                Log.d(TAG, "Exist deviceId=" + deviceId);
                callLaunchScreen();
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
    public void authenticationFailure() {

    }

    @Override
    public void responseErrorPresenter(int errorCode) {
        new ErrorResponseHandler().processFailureResponseCode(this, errorCode);
    }

    private void callLocationManager() {
        if (hasAccessTo(Manifest.permission.ACCESS_FINE_LOCATION) && hasAccessTo(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{PermissionUtils.LOCATION_PERMISSION},
                    PermissionUtils.PERMISSION_REQUEST_LOCATION);
            return;
        }

        long mLocTrackingInterval = 1000; // 5 sec
        float trackingDistance = 1;
        LocationAccuracy trackingAccuracy = LocationAccuracy.HIGH;

        LocationParams.Builder builder = new LocationParams.Builder()
                .setAccuracy(trackingAccuracy)
                .setDistance(trackingDistance)
                .setInterval(mLocTrackingInterval);

        SmartLocation.with(this)
                .location()
                // .continuous()
                .config(builder.build())
                .start(location -> {
                    if (null != location) {
                        this.location = location;
                        callLaunchScreen();
                        try { // pseudo code
                            SmartLocation.with(splashScreen).location().stop();
                            Log.e("SmartLocation: ", "Stopped");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Log.e("Location found: ", "Location detected: Lat: " + location.getLatitude() + ", Lng: " + location.getLongitude());
                    }
                });
    }

    private boolean hasAccessTo(String permissionType) {
        return ActivityCompat.checkSelfPermission(this, permissionType) != PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PermissionUtils.PERMISSION_REQUEST_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    callLocationManager();
                } else {
                    //code for deny
                    if (ActivityCompat.shouldShowRequestPermissionRationale(SplashScreen.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("Permission needed")
                                .setMessage(getString(R.string.gps_error_msg))
                                .setPositiveButton("Location Settings", (paramDialogInterface, paramInt) -> {
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", splashScreen.getPackageName(), null);
                                    intent.setData(uri);
                                    startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                                })
                                .setNegativeButton("Cancel", (paramDialogInterface, paramInt) -> splashScreen.finish());
                        builder.show();
                    } else {
                        // user selected Don't ask again checkbox show proper msg
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("Permission needed")
                                .setMessage(getString(R.string.gps_error_msg_final))
                                .setPositiveButton("Location Settings", (paramDialogInterface, paramInt) -> {
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", splashScreen.getPackageName(), null);
                                    intent.setData(uri);
                                    startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                                })
                                .setNegativeButton("Cancel", (paramDialogInterface, paramInt) -> splashScreen.finish());
                        builder.show();
                    }
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PERMISSION_SETTING) {
            if (hasAccessTo(Manifest.permission.ACCESS_FINE_LOCATION) && hasAccessTo(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                //("permissions not granted!")
                finish();
            } else {
                //("permissions granted!")
                callLocationManager();
            }
        }
        if (requestCode == GPS_ENABLE_REQUEST) {
            if (gpsTracker.isLocationEnabled()) {
                callLocationManager();
            } else {
                finish();
            }
        } else {
            finish();
        }
    }

    private void callLaunchScreen() {
        if (!StringUtils.isBlank(deviceId) && null != location) {
            Intent i = new Intent(splashScreen, LaunchActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.putExtra("fcmToken", fcmToken);
            i.putExtra("deviceId", deviceId);
            i.putExtra("latitude", location.getLatitude());
            i.putExtra("longitude", location.getLongitude());
            splashScreen.startActivity(i);
            splashScreen.finish();
        }
        if (null == location) {
            Log.d(TAG, "Location not found");
        }
    }
}
