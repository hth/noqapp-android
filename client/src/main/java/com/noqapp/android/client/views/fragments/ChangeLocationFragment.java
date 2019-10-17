package com.noqapp.android.client.views.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.google.android.gms.maps.model.LatLng;
import com.noqapp.android.client.R;
import com.noqapp.android.client.utils.AppUtils;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.client.utils.FabricEvents;
import com.noqapp.android.client.utils.GPSTracker;
import com.noqapp.android.client.views.activities.LaunchActivity;
import com.noqapp.android.client.views.adapters.GooglePlacesAutocompleteAdapter;

public class ChangeLocationFragment extends Fragment implements GPSTracker.LocationCommunicator {
    private double lat, log;
    private String city = "";

    public ChangeLocationFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_change_location, container, false);
        GPSTracker gpsTracker = new GPSTracker(getActivity(), this);
        if (gpsTracker.isLocationEnabled()) {
            gpsTracker.getLocation();
        } else {
            gpsTracker.showSettingsAlert();
        }

        TextView tv_auto = view.findViewById(R.id.tv_auto);
        ImageView actionbarBack = view.findViewById(R.id.actionbarBack);
        actionbarBack.setOnClickListener((View v) -> {
            if (TextUtils.isEmpty(LaunchActivity.getLaunchActivity().cityName)) {
                lat = Constants.DEFAULT_LATITUDE;
                log = Constants.DEFAULT_LONGITUDE;
                city = Constants.DEFAULT_CITY;
            } else {
                lat = LaunchActivity.getLaunchActivity().latitude;
                log = LaunchActivity.getLaunchActivity().longitude;
                city = LaunchActivity.getLaunchActivity().cityName;
                AppUtils.hideKeyBoard(getActivity());
            }
            LaunchActivity.getLaunchActivity().updateLocationInfo(lat, log, city);
        });
        tv_auto.setOnClickListener((View v) -> {
            if (TextUtils.isEmpty(LaunchActivity.getLaunchActivity().cityName)) {
                lat = Constants.DEFAULT_LATITUDE;
                log = Constants.DEFAULT_LONGITUDE;
                city = Constants.DEFAULT_CITY;
            } else {
                lat = LaunchActivity.getLaunchActivity().latitude;
                log = LaunchActivity.getLaunchActivity().longitude;
                city = LaunchActivity.getLaunchActivity().cityName;
                AppUtils.hideKeyBoard(getActivity());
            }
        });
        AutoCompleteTextView autoCompleteTextView = view.findViewById(R.id.autoCompleteTextView);
        autoCompleteTextView.setAdapter(new GooglePlacesAutocompleteAdapter(getActivity(), R.layout.list_item));
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    String city_name = (String) parent.getItemAtPosition(position);
                    LatLng latLng = AppUtils.getLocationFromAddress(getActivity(), city_name);
                    if (null != latLng) {
                        lat = latLng.latitude;
                        log = latLng.longitude;
                        LaunchActivity.getLaunchActivity().updateLocationInfo(lat, log, city_name);
                        //finish();

                    } else {
                        //lat = LaunchActivity.getLaunchActivity().getDefaultLatitude();
                        //  log = LaunchActivity.getLaunchActivity().getDefaultLongitude();
                    }
                    city = city_name;
                    AppUtils.hideKeyBoard(getActivity());
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
//                        lat = LaunchActivity.getLaunchActivity().latitude;
//                        log = LaunchActivity.getLaunchActivity().longitude;
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
        if (AppUtils.isRelease()) {
            Answers.getInstance().logCustom(new CustomEvent(FabricEvents.EVENT_CHANGE_LOCATION));
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        LaunchActivity.getLaunchActivity().getSupportActionBar().hide();
    }

    @Override
    public void updateLocationUI() {
        // Log.e("Location update", "Lat: " + String.valueOf(gpsTracker.getLatitude()) + " Long: " + String.valueOf(gpsTracker.getLongitude()) + " City: " + gpsTracker.getCityName());

    }
}
