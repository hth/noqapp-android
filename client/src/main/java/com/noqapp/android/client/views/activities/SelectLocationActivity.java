package com.noqapp.android.client.views.activities;

import com.noqapp.android.client.R;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.GPSTracker;
import com.noqapp.android.client.views.adapters.GooglePlacesAutocompleteAdapter;

import com.google.android.gms.maps.model.LatLng;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;


public class SelectLocationActivity extends BaseActivity implements GPSTracker.LocationCommunicator {
    GPSTracker gpsTracker;
    AutoCompleteTextView autoCompleteTextView;
    private double lat, log;
    private String city = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        initActionsViews(false);
        gpsTracker = new GPSTracker(this, this);
        if (gpsTracker.isLocationEnabled()) {
            gpsTracker.getLocation();
        } else {
            gpsTracker.showSettingsAlert();
        }

        TextView tv_auto = findViewById(R.id.tv_auto);
        tv_auto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(LaunchActivity.getLaunchActivity().cityName)) {
                    lat = LaunchActivity.getLaunchActivity().getDefaultLatitude();
                    log = LaunchActivity.getLaunchActivity().getDefaultLongitude();
                    city = LaunchActivity.getLaunchActivity().getDefaultCity();
                } else {
                    lat = LaunchActivity.getLaunchActivity().latitute;
                    log = LaunchActivity.getLaunchActivity().longitute;
                    city = LaunchActivity.getLaunchActivity().cityName;
                    new AppUtilities().hideKeyBoard(SelectLocationActivity.this);
                }
            }
        });
        autoCompleteTextView = findViewById(R.id.autoCompleteTextView);
        autoCompleteTextView.setAdapter(new GooglePlacesAutocompleteAdapter(this, R.layout.list_item));
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    String city_name = (String) parent.getItemAtPosition(position);
                    LatLng latLng = AppUtilities.getLocationFromAddress(SelectLocationActivity.this, city_name);
                    if (null != latLng) {
                        lat = latLng.latitude;
                        log = latLng.longitude;
                        LaunchActivity.getLaunchActivity().updateLocationInfo(lat,log,city_name);
                        finish();
                    } else {
                        //lat = LaunchActivity.getLaunchActivity().getDefaultLatitude();
                      //  log = LaunchActivity.getLaunchActivity().getDefaultLongitude();
                    }
                    city = city_name;
                    new AppUtilities().hideKeyBoard(SelectLocationActivity.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        autoCompleteTextView.setThreshold(3);
        autoCompleteTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_LEFT = 0;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (autoCompleteTextView.getRight() - autoCompleteTextView.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        autoCompleteTextView.setText("");
                        return true;
                    }
//                    if (event.getRawX() <= (10+autoCompleteTextView.getLeft() + autoCompleteTextView.getCompoundDrawables()[DRAWABLE_LEFT].getBounds().width())) {
//                        // your action here
//                        lat = LaunchActivity.getLaunchActivity().latitute;
//                        log = LaunchActivity.getLaunchActivity().longitute;
//                        city = LaunchActivity.getLaunchActivity().cityName;
//                        autoCompleteTextView.setText(city);
//                        getNearMeInfo(city, String.valueOf(lat), String.valueOf(log));
//                        new AppUtilities().hideKeyBoard(this);
//                        return true;
//                    }
                }
                return false;
            }
        });
    }

    @Override
    public void updateLocationUI() {
        Log.e("Location update", "Lat: " + String.valueOf(gpsTracker.getLatitude()) + " Long: " + String.valueOf(gpsTracker.getLongitude()) + " City: " + gpsTracker.getCityName());

    }
}
