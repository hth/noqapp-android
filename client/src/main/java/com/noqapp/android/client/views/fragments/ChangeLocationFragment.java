package com.noqapp.android.client.views.fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.noqapp.android.client.R;
import com.noqapp.android.client.location.LocationManager;
import com.noqapp.android.client.utils.AnalyticsEvents;
import com.noqapp.android.client.utils.AppUtils;
import com.noqapp.android.client.views.activities.AppInitialize;
import com.noqapp.android.client.views.activities.LaunchActivity;
import com.noqapp.android.client.views.adapters.GooglePlacesAutocompleteAdapter;
import com.noqapp.android.client.views.pojos.LocationPref;
import com.noqapp.android.common.utils.GeoIP;

import kotlin.Unit;
import kotlin.jvm.functions.Function4;

public class ChangeLocationFragment extends Fragment implements Function4<String, String, Double, Double, Unit> {
    private double lat, lng;
    private String city = "";
    private AutoCompleteTextView autoCompleteTextView;
    private ChangeLocationFragmentInteractionListener changeLocationFragmentInteractionListener;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof LaunchActivity) {
            changeLocationFragmentInteractionListener = (ChangeLocationFragmentInteractionListener) context;
        } else
            throw new IllegalStateException("LaunchActivity must implement ChangeLocationFragmentInteractionListener.");
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
            if (TextUtils.isEmpty(AppInitialize.cityName)) {
                LocationPref locationPref = AppInitialize.getLocationPreference();
                lat = locationPref.getLatitude();
                lng = locationPref.getLongitude();
                city = locationPref.getCity();
            } else {
                lat = AppInitialize.location.getLatitude();
                lng = AppInitialize.location.getLongitude();
                city = AppInitialize.cityName;
                AppUtils.hideKeyBoard(getActivity());
            }
            changeLocationFragmentInteractionListener.updateLocationInfo(lat, lng, city);
        });

        tv_auto.setOnClickListener((View v) -> {
            LocationManager.INSTANCE.fetchCurrentLocationAddress(requireContext(), this);
        });

        autoCompleteTextView.setAdapter(new GooglePlacesAutocompleteAdapter(getActivity(), R.layout.list_item));
        autoCompleteTextView.setOnItemClickListener((parent, view1, position, id) -> {
            try {
                String cityName = (String) parent.getItemAtPosition(position);
                GeoIP latLng = AppUtils.getLocationFromAddress(getActivity(), cityName);
                if (null != latLng) {
                    lat = latLng.getLatitude();
                    lng = latLng.getLongitude();
                    changeLocationFragmentInteractionListener.updateLocationInfo(lat, lng, cityName);
                }
                city = cityName;
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
        LocationManager.INSTANCE.startLocationUpdate(requireContext());
        LaunchActivity.getLaunchActivity().getSupportActionBar().hide();
    }

    @Override
    public void onStop() {
        LocationManager.INSTANCE.stopLocationUpdate(requireContext());
        super.onStop();
    }

    @Override
    public Unit invoke(String address, String cityName, Double latitude, Double longitude) {
        if (latitude == 0 && longitude == 0) {
            LocationPref locationPref = AppInitialize.getLocationPreference();
            lat = locationPref.getLatitude();
            lng = locationPref.getLongitude();
            city = locationPref.getCity();
        } else {
            lat = latitude;
            lng = longitude;
            city = cityName;
        }

        changeLocationFragmentInteractionListener.updateLocationInfo(latitude, longitude, city);
        AppUtils.setAutoCompleteText(autoCompleteTextView, city);
        AppUtils.hideKeyBoard(requireActivity());
        return null;
    }

    public interface ChangeLocationFragmentInteractionListener {
        void updateLocationInfo(Double latitude, Double longitude, String address);
    }
}
