package com.noqapp.android.client.utils;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.noqapp.android.client.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Create this Class from tutorial :
 * http://www.androidhive.info/2012/07/android-gps-location-manager-tutorial
 *
 * For Geocoder read this : http://stackoverflow.com/questions/472313/android-reverse-geocoding-getfromlocation
 *
 */

public class GPSTracker implements LocationListener {
    public static final int GPS_ENABLE_REQUEST = 0x1001;
    private String mPermission = Manifest.permission.ACCESS_FINE_LOCATION;
    public final int REQUEST_CODE_PERMISSION = 2;
    // Get Class Name
    private static String TAG = GPSTracker.class.getName();

    private final Activity mContext;

    // flag for GPS Status
    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;

    Location location;
    double latitude = 0;
    double longitude = 0;
    public interface LocationCommunicator {
        void updateLocationUI();
    }


    // The minimum distance to change updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

    private LocationManager locationManager;


    private LocationCommunicator locationCommunicator;

    public String getCityName() {
        return cityName;
    }

    private String cityName = "";

    public GPSTracker(Activity context,LocationCommunicator locationCommunicator) {
        this.mContext = context;
        this.locationCommunicator = locationCommunicator;
    }

    /**
     * Try to get my current location by GPS or Network Provider
     */
    public void getLocation() {

        try {
            locationManager = (LocationManager) mContext
                    .getSystemService(mContext.LOCATION_SERVICE);

            // Getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // Getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // No network provider is enabled
            } else {
                // this.canGetLocation = true;
                if (isNetworkEnabled) {
                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(mContext, new String[]{mPermission},
                                REQUEST_CODE_PERMISSION);
                        return;
                    }
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network");
                    if (locationManager != null) {

                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            getAddress(latitude,longitude);
                            locationCommunicator.updateLocationUI();
                        }
                    }
                }
                // If GPS enabled, get latitude/longitude using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                                && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(mContext, new String[]{mPermission},
                                    REQUEST_CODE_PERMISSION);
                            return;
                        }
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                                getAddress(latitude,longitude);
                                locationCommunicator.updateLocationUI();
                            }
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Update GPSTracker latitude and longitude
     */
    public void updateGPSCoordinates() {
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
    }

    /**
     * GPSTracker latitude getter and setter
     * @return latitude
     */
    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }

        return latitude;
    }

    /**
     * GPSTracker longitude getter and setter
     * @return
     */
    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }

        return longitude;
    }


    public boolean isLocationEnabled() {
        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    /**
     * Stop using GPS listener
     * Calling this method will stop using GPS in your app
     */
    public void stopUsingGPS() {
        if (locationManager != null) {
            locationManager.removeUpdates(GPSTracker.this);
        }
    }

    public void showSettingsAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
        dialog.setTitle(mContext.getString(R.string.enable_gps))
                .setMessage(mContext.getString(R.string.message_enable_gps))
                .setPositiveButton(mContext.getString(R.string.btn_location_setting), (paramDialogInterface, paramInt) -> {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    mContext.startActivityForResult(myIntent, GPS_ENABLE_REQUEST);
                })
                .setNegativeButton(mContext.getString(R.string.cancel_button), (paramDialogInterface, paramInt) -> {
                });
        dialog.show();
    }


    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        getAddress(latitude,longitude);
        locationCommunicator.updateLocationUI();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    public void getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            cityName = addresses.get(0).getAddressLine(0);
            if (!TextUtils.isEmpty(obj.getLocality()) && !TextUtils.isEmpty(obj.getSubLocality())) {
                cityName = obj.getSubLocality() + ", " + obj.getLocality();
            } else {
                if (!TextUtils.isEmpty(obj.getSubLocality())) {
                    cityName = obj.getSubLocality();
                } else if (!TextUtils.isEmpty(obj.getLocality())) {
                    cityName = obj.getLocality();
                } else {
                    cityName = addresses.get(0).getAddressLine(0);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

