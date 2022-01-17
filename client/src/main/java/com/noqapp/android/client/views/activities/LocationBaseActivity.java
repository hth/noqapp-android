package com.noqapp.android.client.views.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.noqapp.android.client.BuildConfig;
import com.noqapp.android.client.location.LocationManager;

import static com.noqapp.android.client.utils.Constants.REQUEST_CHECK_SETTINGS;

/**
 * Base class to perform location related work
 * It can be used as a base class where ever current address, latitude, longitude comes into picture
 * Created by Vivek Jha on 23/02/2021
 */
public abstract class LocationBaseActivity extends BaseActivity {
    public abstract void displayAddressOutput(
            String addressOutput,
            String countryShortName,
            String area,
            String town,
            String district,
            String state,
            String stateShortName,
            Double latitude,
            Double longitude
    );

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private boolean shouldRedirectToSettings = false;
    private boolean shownEnableLocationDialog = false;

    public abstract void locationPermissionRequired();

    protected abstract void locationPermissionGranted();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        getCurrentLocation();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("Location tag", "service started");

    }

    public void getCurrentLocation() {
        if (!checkLocationPermission()) {
            locationPermissionRequired();
        } else {
            locationPermissionGranted();
            checkLocationSettings();
        }
    }

    public void showSnackbar(int mainTextStringId, int actionStringId, View.OnClickListener listener) {
        Snackbar.make(findViewById(android.R.id.content), getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener)
                .show();
    }

    public void showSnackbar(int mainTextStringId) {
        Snackbar.make(findViewById(android.R.id.content), getString(mainTextStringId),
                Snackbar.LENGTH_SHORT)
                .show();
    }

    private boolean checkLocationPermission() {
        int permissionState = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    public void requestPermissions() {
        boolean shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (shouldProvideRationale) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        } else {
            if (shouldRedirectToSettings) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.fromParts("package", BuildConfig.APPLICATION_ID, null));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_PERMISSIONS_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != REQUEST_PERMISSIONS_REQUEST_CODE) return;

        if (grantResults.length == 0) {
            Log.i("LocationRequest", "User interaction was cancelled.");
        } else if (PackageManager.PERMISSION_GRANTED == grantResults[0]) {
            getCurrentLocation();
        } else {
            shouldRedirectToSettings = true;
            locationPermissionRequired();
        }
    }

    private void checkLocationSettings() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setNeedBle(false);

        Task<LocationSettingsResponse> task = LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());
        task.addOnSuccessListener(response -> {
            LocationSettingsStates locationSettingsStates = response.getLocationSettingsStates();
            if (locationSettingsStates.isLocationPresent()) {
                if (!NoQueueClientApplication.isLocationChangedManually()) {
                    LocationManager.INSTANCE.fetchCurrentLocationAddress(this, (address, countryShortName, area, town, district, state, stateShortName, latitude, longitude) -> {
                        displayAddressOutput(address, countryShortName, area, town, district, state, stateShortName, latitude, longitude);
                        return null;
                    });
                }
            }
        });

        task.addOnFailureListener(this, e -> {
            try {
                // Cast to a resolvable exception.
                if (!shownEnableLocationDialog) {
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    resolvable.startResolutionForResult(LocationBaseActivity.this, REQUEST_CHECK_SETTINGS);
                    shownEnableLocationDialog = true;
                }
            } catch (IntentSender.SendIntentException ie) {
                // Ignore the error.
            } catch (ClassCastException ce) {
                // Ignore, should be an impossible error.
            }
        });
    }

    protected void getMapLocation(Double latitude, Double longitude) {
        LocationManager.INSTANCE.getLocationAddress(this, latitude, longitude, (address, countryShortName, area, town, district, state, stateShortName, lat, lng) -> {
            displayAddressOutput(address, countryShortName, area, town, district, state, stateShortName, lat, lng);
            return null;
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_CHECK_SETTINGS == requestCode) {
            shownEnableLocationDialog = false;
            if (Activity.RESULT_OK == resultCode) {
                getCurrentLocation();
            } else {
                locationPermissionRequired();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocationManager.INSTANCE.stopLocationUpdate(this);
        Log.d("Location tag", "service stopped");
    }
}
