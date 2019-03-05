package com.noqapp.android.client.views.activities;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

public abstract class LocationActivity extends NoQueueBaseActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    private static final int REQUEST_CODE_PERMISSION = 2;
    private static final String TAG = "LocationActivity";
    public double latitute = 0;
    public double longitute = 0;
    public String cityName = "";
    private String mPermission = Manifest.permission.ACCESS_FINE_LOCATION;
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 60 * 2 * 1000 * 10;  /* 10*6 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */
    private LocationManager locationManager;
    private final int GPS_ENABLE_REQUEST = 0x1001;

    public double getDefaultLatitude() {
        return 19.0760;
    }

    public double getDefaultLongitude() {
        return 72.8777;
    }

    public String getDefaultCity() {
        return "Mumbai";
    }

    private FusedLocationProviderClient fusedLocationProviderClient;

    // TODO @Chandra
    // Please check this link too ->https://stackoverflow.com/a/3145655/3912847
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        //check whether location service is enable or not
        checkLocation();
    }

    public abstract void updateLocationUI();

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{mPermission},
                    REQUEST_CODE_PERMISSION);
            return;
        }

        startLocationUpdates();

        if (mLocation == null) {
            startLocationUpdates();
        }
        if (mLocation != null) {
            latitute = mLocation.getLatitude();
            longitute = mLocation.getLongitude();
            String msg = "Updated Location: " +
                    Double.toString(latitute) + "," +
                    Double.toString(longitute);
            getAddress(latitute, longitute);
            updateLocationUI();
        } else {
            Toast.makeText(this, "Location not Detected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection Suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed. Error: " + connectionResult.getErrorCode());
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    protected void startLocationUpdates() {
        // Create the location request
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL)
                .setSmallestDisplacement(500);

        // Request location updates
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{mPermission},
                    REQUEST_CODE_PERMISSION);
            return;
        }
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations, this can be null.
                        if (location != null) {
                            mLocation = location;
                        }
                    }
                });
    }

    @Override
    public void onLocationChanged(Location location) {
        latitute = location.getLatitude();
        longitute = location.getLongitude();
        getAddress(latitute, longitute);
        updateLocationUI();
    }

    public void checkLocation() {
        if (!isLocationEnabled())
            showAlert();
        //return isLocationEnabled();
    }

    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable GPS")
                .setMessage("Gps is disabled, in order to use the application properly you need to enable GPS of your device")
                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(myIntent, GPS_ENABLE_REQUEST);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    }
                });
        dialog.show();
    }

    private boolean isLocationEnabled() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission was granted.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        onConnected(getIntent().getExtras());
                    }
                } else {
                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GPS_ENABLE_REQUEST) {
            if (isLocationEnabled()) {
                if (mGoogleApiClient != null) {
                    mGoogleApiClient.connect();
                }
                //onConnected(getIntent().getExtras());
            }

        }
    }

    public void getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}