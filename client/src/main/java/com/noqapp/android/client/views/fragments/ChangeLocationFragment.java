package com.noqapp.android.client.views.fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.model.LatLng;
import com.noqapp.android.client.R;
import com.noqapp.android.client.utils.AppUtils;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.client.utils.AnalyticsEvents;
import com.noqapp.android.client.utils.GPSTracker;
import com.noqapp.android.client.views.activities.LaunchActivity;
import com.noqapp.android.client.views.adapters.GooglePlacesAutocompleteAdapter;

public class ChangeLocationFragment extends Fragment implements GPSTracker.LocationCommunicator {
    private double lat, lng;
    private String city = "";
    private GPSTracker gpsTracker;
    private AutoCompleteTextView autoCompleteTextView;

    public ChangeLocationFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_change_location, container, false);
        TextView tv_auto = view.findViewById(R.id.tv_auto);
        ImageView actionbarBack = view.findViewById(R.id.actionbarBack);
        TextView tv_toolbar_title = view.findViewById(R.id.tv_toolbar_title);
        tv_toolbar_title.setText(getString(R.string.screen_change_location));
        autoCompleteTextView = view.findViewById(R.id.autoCompleteTextView);
        actionbarBack.setOnClickListener((View v) -> {
            if (TextUtils.isEmpty(LaunchActivity.getLaunchActivity().cityName)) {
                lat = Constants.DEFAULT_LATITUDE;
                lng = Constants.DEFAULT_LONGITUDE;
                city = Constants.DEFAULT_CITY;
            } else {
                lat = LaunchActivity.getLaunchActivity().latitude;
                lng = LaunchActivity.getLaunchActivity().longitude;
                city = LaunchActivity.getLaunchActivity().cityName;
                AppUtils.hideKeyBoard(getActivity());
            }
            LaunchActivity.getLaunchActivity().updateLocationInfo(lat, lng, city);
        });
        tv_auto.setOnClickListener((View v) -> {
            gpsTracker = new GPSTracker(getActivity(), this);
            if (gpsTracker.isLocationEnabled()) {
                gpsTracker.getLocation();
            } else {
                gpsTracker.showSettingsAlert();
            }

        });

        autoCompleteTextView.setAdapter(new GooglePlacesAutocompleteAdapter(getActivity(), R.layout.list_item));
        autoCompleteTextView.setOnItemClickListener((parent, view1, position, id) -> {
            try {
                String city_name = (String) parent.getItemAtPosition(position);
                LatLng latLng = AppUtils.getLocationFromAddress(getActivity(), city_name);
                if (null != latLng) {
                    lat = latLng.latitude;
                    lng = latLng.longitude;
                    LaunchActivity.getLaunchActivity().updateLocationInfo(lat, lng, city_name);
                    //finish();
                } else {
                    //lat = LaunchActivity.getLaunchActivity().getDefaultLatitude();
                    //lng = LaunchActivity.getLaunchActivity().getDefaultLongitude();
                }
                city = city_name;
                AppUtils.hideKeyBoard(getActivity());
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
        autoCompleteTextView.setThreshold(3);
        autoCompleteTextView.setOnTouchListener((v, event) -> {
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
//                        lng = LaunchActivity.getLaunchActivity().longitude;
//                        city = LaunchActivity.getLaunchActivity().cityName;
//                        autoCompleteTextView.setText(city);
//                        getNearMeInfo(city, String.valueOf(lat), String.valueOf(lng));
//                        new AppUtilities().hideKeyBoard(this);
//                        return true;
//                    }
            }
            return false;
        });
        if (AppUtils.isRelease()) {
            AnalyticsEvents.logContentEvent(AnalyticsEvents.EVENT_CHANGE_LOCATION);
        }
        autoCompleteTextView.requestFocus();
        InputMethodManager imgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        LaunchActivity.getLaunchActivity().getSupportActionBar().hide();
    }

    @Override
    public void updateLocationUI() {
        // Log.e("Location update", "Lat: " + String.valueOf(gpsTracker.getLatitude()) + " Lng: " + String.valueOf(gpsTracker.getLongitude()) + " City: " + gpsTracker.getCityName());
        if (gpsTracker.getLatitude() == 0) {
            lat = Constants.DEFAULT_LATITUDE;
            lng = Constants.DEFAULT_LONGITUDE;
            city = Constants.DEFAULT_CITY;
        } else {
            lat = gpsTracker.getLatitude();
            lng = gpsTracker.getLongitude();
            city = gpsTracker.getCityName();
        }
        LaunchActivity.getLaunchActivity().updateLocationInfo(lat, lng, city);
        AppUtils.setAutoCompleteText(autoCompleteTextView, city);
        AppUtils.hideKeyBoard(getActivity());
    }

    @Override
    public void onDetach() {
        if (null != gpsTracker) {
            gpsTracker.stopUsingGPS();
        }
        super.onDetach();
    }

    @Override
    public void onStop() {
        if (null != gpsTracker) {
            gpsTracker.stopUsingGPS();
        }
        super.onStop();
    }
}
