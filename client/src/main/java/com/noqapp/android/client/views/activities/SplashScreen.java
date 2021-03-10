package com.noqapp.android.client.views.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.noqapp.android.client.R;
import com.noqapp.android.client.model.DeviceApiCall;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.client.utils.ErrorResponseHandler;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.pojos.LocationPref;
import com.noqapp.android.common.beans.DeviceRegistered;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.body.DeviceToken;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.presenter.DeviceRegisterPresenter;
import com.noqapp.android.common.utils.CommonHelper;
import com.noqapp.android.common.utils.NetworkUtil;
import com.noqapp.android.common.utils.PermissionUtils;

import org.apache.commons.lang3.StringUtils;

public class SplashScreen extends AppCompatActivity implements DeviceRegisterPresenter {

    static SplashScreen splashScreen;
    private String TAG = SplashScreen.class.getSimpleName();
    private static String tokenFCM = "";
    private static String deviceId = "";
    private final int REQUEST_PERMISSION_SETTING = 23;
    private Location location;
    private LocationPref locationPref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        splashScreen = this;
        LottieAnimationView animationView = findViewById(R.id.animation_view);
        animationView.setAnimation("data.json");
        animationView.playAnimation();
        animationView.setRepeatCount(10);
        location = new Location("");
        locationPref = AppInitialize.getLocationPreference();
        location.setLatitude(locationPref.getLatitude());
        location.setLongitude(locationPref.getLongitude());

        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this, token -> {
            tokenFCM = token;
            Log.d(TAG, "New FCM Token=" + tokenFCM);
            sendRegistrationToServer(tokenFCM);
        });

        if (StringUtils.isBlank(tokenFCM) && new NetworkUtil(this).isNotOnline()) {
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
            deviceId = deviceRegistered.getDeviceId();
            Log.d(TAG, "Server Created deviceId=" + deviceId + "\n DeviceRegistered: " + deviceRegistered);
            String cityName = CommonHelper.getAddress(deviceRegistered.getGeoPointOfQ().getLat(), deviceRegistered.getGeoPointOfQ().getLon(), this);
            Log.d(TAG, "Splash City Name =" + cityName);

            LocationPref locationPref = AppInitialize.getLocationPreference()
                    .setCity(cityName)
                    .setLatitude(deviceRegistered.getGeoPointOfQ().getLat())
                    .setLongitude(deviceRegistered.getGeoPointOfQ().getLon());
            AppInitialize.setLocationPreference(locationPref);
            AppInitialize.setDeviceID(deviceId);
            location.setLatitude(locationPref.getLatitude());
            location.setLongitude(locationPref.getLongitude());
            callLaunchScreen();
        } else {
            Log.e("Device register error: ", deviceRegistered.toString());
            new CustomToast().showToast(this, "Device register error: ");
        }
    }

    private void sendRegistrationToServer(String refreshToken) {
        if (new NetworkUtil(this).isOnline()) {
            DeviceToken deviceToken = new DeviceToken(refreshToken, Constants.appVersion(), location);
            deviceId = AppInitialize.getDeviceId();
            if (TextUtils.isEmpty(deviceId)) {
                /* Call this api only once in life time. */
                DeviceApiCall deviceModel = new DeviceApiCall();
                deviceModel.setDeviceRegisterPresenter(this);
                if (UserUtils.isLogin()) {
                    deviceModel.register(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), deviceToken);
                } else {
                    deviceModel.register(deviceToken);
                }
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

    private boolean hasAccessTo(String permissionType) {
        return ActivityCompat.checkSelfPermission(this, permissionType) != PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PermissionUtils.PERMISSION_REQUEST_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    callLaunchScreen();
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
                finish();
            } else {
                callLaunchScreen();
            }
        }
    }

    private void callLaunchScreen() {
        if (!StringUtils.isBlank(deviceId) && null != location) {
            Intent i = new Intent(splashScreen, LaunchActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.putExtra(AppInitialize.TOKEN_FCM, tokenFCM);
            i.putExtra("deviceId", deviceId);
            splashScreen.startActivity(i);
            splashScreen.finish();
        }
    }
}
