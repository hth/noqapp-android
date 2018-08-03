package com.noqapp.android.client.utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import java.util.List;
import java.util.Locale;

public class GPSTracker implements LocationListener {

    private final Context mContext;
    // Flag for GPS status
    private boolean canGetLocation = false;

    private Location location; // Location
    private double latitude; // Latitude
    private double longitude; // Longitude

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1000; // 1000 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 10; // 10 minute

    private LocationManager locationManager;

    public GPSTracker(Context context) {
        this.mContext = context;
        getLocation();
    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) mContext
                    .getSystemService(Context.LOCATION_SERVICE);

            // Getting GPS status
            boolean isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // Getting network status
            boolean isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // No network provider is enabled
            } else {
                this.canGetLocation = true;
                if (isNetworkEnabled) {
                    try {
                        locationManager.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    } catch (SecurityException e) {
                        e.printStackTrace();
                    }
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        try {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        } catch (SecurityException e) {
                            e.printStackTrace();
                        }
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                // If GPS enabled, get latitude/longitude using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        try {
                            locationManager.requestLocationUpdates(
                                    LocationManager.GPS_PROVIDER,
                                    MIN_TIME_BW_UPDATES,
                                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        } catch (SecurityException e) {
                            e.printStackTrace();
                        }
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            try {
                                location = locationManager
                                        .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            } catch (SecurityException e) {
                                e.printStackTrace();
                            }
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }


    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app.
     */
    public void stopUsingGPS() {
        if (locationManager != null) {
            locationManager.removeUpdates(GPSTracker.this);
        }
    }


    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }
        return latitude;
    }


    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }
        return longitude;
    }

    /**
     * Function to check GPS/Wi-Fi enabled
     *
     * @return boolean
     */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            //Toast.makeText(mContext,"Location has been changed",Toast.LENGTH_LONG).show();
            Log.e("Location:", "There is change in location");
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }


    @Override
    public void onProviderDisabled(String provider) {
    }


    @Override
    public void onProviderEnabled(String provider) {
    }

    public String getAddress(double lat, double lng) {
        String cityName = "";
        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            if (null != addresses && addresses.size() > 0) {
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
            }
        } catch (Exception e) {
            e.printStackTrace();
            return cityName;
        }
        return cityName;
    }
}
