package com.noqapp.android.client.views.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.noqapp.android.client.R;
import com.noqapp.android.client.model.DeviceApiCall;
import com.noqapp.android.client.utils.AppUtils;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.client.utils.ErrorResponseHandler;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.pojos.LocationPref;
import com.noqapp.android.client.views.version_2.HomeActivity;
import com.noqapp.android.common.beans.DeviceRegistered;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonUserAddress;
import com.noqapp.android.common.beans.body.DeviceToken;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.presenter.DeviceRegisterPresenter;
import com.noqapp.android.common.utils.CommonHelper;
import com.noqapp.android.common.utils.NetworkUtil;

import org.apache.commons.lang3.StringUtils;

public class SplashScreen extends LocationBaseActivity implements DeviceRegisterPresenter {

    private String TAG = SplashScreen.class.getSimpleName();
    private static String tokenFCM = "";
    private static String deviceId = "";
    private ConstraintLayout clAllowLocationPermission;
    private Button btnAllowLocationPermission;

    @Override
    public void displayAddressOutput(String addressOutput, String countryShortName, String area, String town, String district, String state, String stateShortName, Double latitude, Double longitude) {
        AppInitialize.location.setLatitude(latitude);
        AppInitialize.location.setLongitude(longitude);

        String city = town;
        if (StringUtils.isNotBlank(area)) {
            city = area + ", " + town;
        }

        AppInitialize.cityName = city;
        LocationPref locationPref = AppInitialize.getLocationPreference()
                .setArea(area)
                .setTown(town)
                .setLatitude(latitude)
                .setLongitude(longitude);
        AppInitialize.setLocationPreference(locationPref);

        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this, token -> {
            tokenFCM = token;
            Log.d(TAG, "New FCM Token=" + tokenFCM);
            AppInitialize.setTokenFCM(token);
            sendRegistrationToServer(tokenFCM, AppInitialize.location);
        });
    }

    @Override
    public void locationPermissionRequired() {
        clAllowLocationPermission.setVisibility(View.VISIBLE);
    }

    @Override
    protected void locationPermissionGranted() {
        clAllowLocationPermission.setVisibility(View.GONE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        AppInitialize.setLocationChangedManually(false);

        LottieAnimationView animationView = findViewById(R.id.animation_view);
        animationView.setAnimation("data.json");
        animationView.playAnimation();
        animationView.setRepeatCount(10);
        clAllowLocationPermission = findViewById(R.id.cl_location_access_required);
        btnAllowLocationPermission = findViewById(R.id.btn_allow_location_access);

        btnAllowLocationPermission.setOnClickListener(v -> {
            requestPermissions();
        });

    }

    @Override
    public void deviceRegisterError() {
        callLaunchScreen();
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
            AppInitialize.setDeviceID(deviceId);

            Log.d(TAG, "Server Created deviceId=" + deviceId + "\n DeviceRegistered: " + deviceRegistered);
            JsonUserAddress jsonUserAddress = CommonHelper.getAddress(deviceRegistered.getGeoPointOfQ().getLat(), deviceRegistered.getGeoPointOfQ().getLon(), this);
            Log.d(TAG, "Splash City Name =" + jsonUserAddress.getLocationAsString());
            LocationPref locationPref = AppInitialize.getLocationPreference();

            if (0.0 == locationPref.getLatitude() && 0.0 == locationPref.getLatitude()) {
                locationPref
                        .setArea(jsonUserAddress.getArea())
                        .setTown(jsonUserAddress.getTown())
                        .setLatitude(deviceRegistered.getGeoPointOfQ().getLat())
                        .setLongitude(deviceRegistered.getGeoPointOfQ().getLon());
                AppInitialize.setLocationPreference(locationPref);
                Location location = new Location("");
                location.setLatitude(locationPref.getLatitude());
                location.setLongitude(locationPref.getLongitude());
            }


            callLaunchScreen();
        } else {
            Log.e("Device register error: ", deviceRegistered.toString());
            new CustomToast().showToast(this, "Device register error: ");
        }
    }

    private void sendRegistrationToServer(String refreshToken, Location location) {
        if (new NetworkUtil(this).isOnline()) {
            DeviceToken deviceToken = new DeviceToken(refreshToken, Constants.appVersion(), AppUtils.getSelectedLanguage(this), location);
            deviceId = AppInitialize.getDeviceId();
            if (TextUtils.isEmpty(deviceId)) {
                /* Call this api only once in life time. */
                DeviceApiCall deviceApiCall = new DeviceApiCall();
                deviceApiCall.setDeviceRegisterPresenter(this);
                if (UserUtils.isLogin()) {
                    deviceApiCall.register(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), deviceToken);
                } else {
                    deviceApiCall.register(deviceToken);
                }
            } else {
                Log.d(TAG, "Existing did " + deviceId);
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
    public void responseErrorPresenter(int errorCode) {
        new ErrorResponseHandler().processFailureResponseCode(this, errorCode);
    }

    private void callLaunchScreen() {
        if (!StringUtils.isBlank(deviceId)) {
            Intent i = new Intent(this, HomeActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.putExtra(AppInitialize.TOKEN_FCM, tokenFCM);
            i.putExtra("deviceId", deviceId);
            startActivity(i);
            finish();
        }
    }

}
