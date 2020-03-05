package com.noqapp.android.client.views.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.android.PolyUtil;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.TravelMode;
import com.noqapp.android.client.R;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class MapFragment extends Fragment implements OnMapReadyCallback {
    private LatLng source;
    private LatLng destination;
    private static final int overview = 0;

    public static MapFragment getInstance(LatLng source, LatLng destination) {
        MapFragment mapFragment = new MapFragment();
        Bundle b = new Bundle();
        b.putParcelable("source", source);
        b.putParcelable("destination", destination);
        mapFragment.setArguments(b);
        return mapFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View itemView = inflater.inflate(R.layout.frag_map, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        return itemView;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        GoogleMap mMap = googleMap;
        source = (LatLng) getArguments().get("source");
        destination = (LatLng) getArguments().get("destination");

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);

        TravelMode travelMode = TravelMode.DRIVING;
        DirectionsResult results = getDirectionsDetails(travelMode);
        if (results != null) {
            addPolyline(results, googleMap);
            positionCamera(results, googleMap);
            addMarkersToMap(results, googleMap, travelMode);
        }
    }

    private void positionCamera(DirectionsResult results, GoogleMap mMap) {
        try {
            DirectionsRoute route = results.routes[overview];
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(route.legs[overview].endLocation.lat, route.legs[overview].endLocation.lng), 12));
        } catch (ArrayIndexOutOfBoundsException e) {
            e.getLocalizedMessage();
        }
    }

    private void addMarkersToMap(DirectionsResult results, GoogleMap mMap, TravelMode travelMode) {
        try {
            /* For the start location, the color of marker is GREEN and for the end location, the color of marker is RED. */
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(results.routes[overview].legs[overview].startLocation.lat, results.routes[overview].legs[overview].startLocation.lng))
                    .title(results.routes[overview].legs[overview].startAddress)
                    .icon(getMarkerIcon("#" + Integer.toHexString(ContextCompat.getColor(getContext(), R.color.green)))))
                    .setFlat(true);
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(results.routes[overview].legs[overview].endLocation.lat, results.routes[overview].legs[overview].endLocation.lng))
                    .title(results.routes[overview].legs[overview].endAddress)
                    .icon(getMarkerIcon("#" + Integer.toHexString(ContextCompat.getColor(getContext(), R.color.colorAccent))))
                    .snippet(getEndLocationTitle(results, TravelMode.DRIVING)));
        } catch (ArrayIndexOutOfBoundsException e) {
            e.getLocalizedMessage();

            mMap.addMarker(new MarkerOptions()
                    .position(source)
                    .title("No route information available to destination")
                    .icon(getMarkerIcon("#" + Integer.toHexString(ContextCompat.getColor(getContext(), R.color.green)))));
            mMap.addMarker(new MarkerOptions()
                    .position(destination)
                    .title("Destination route not available")
                    .icon(getMarkerIcon("#" + Integer.toHexString(ContextCompat.getColor(getContext(), R.color.colorAccent)))));

            CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(source, 10);
            mMap.animateCamera(yourLocation);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
    }


    private DirectionsResult getDirectionsDetails(TravelMode travelMode) {
        DateTime now = DateTime.now(DateTimeZone.UTC).plusDays(1);
        try {
            return DirectionsApi.newRequest(getGeoContext())
                    .mode(travelMode)
                    .origin(new com.google.maps.model.LatLng(source.latitude, source.longitude))
                    .destination(new com.google.maps.model.LatLng(destination.latitude, destination.longitude))
                    .departureTime(now)
                    .await();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private GeoApiContext getGeoContext() {
        GeoApiContext geoApiContext = new GeoApiContext();
        return geoApiContext.setQueryRateLimit(3)
                .setApiKey(getString(R.string.google_maps_key))
                .setConnectTimeout(2, TimeUnit.SECONDS)
                .setReadTimeout(2, TimeUnit.SECONDS)
                .setWriteTimeout(2, TimeUnit.SECONDS);
    }

    private void addPolyline(DirectionsResult results, GoogleMap mMap) {
        try {
            List<LatLng> decodedPath = PolyUtil.decode(results.routes[0].overviewPolyline.getEncodedPath());
            PolylineOptions polylineOptions = new PolylineOptions();
            polylineOptions.color(Color.parseColor("#" + Integer.toHexString(ContextCompat.getColor(getContext(), R.color.colorAccent))));
            polylineOptions.addAll(decodedPath);
            mMap.addPolyline(polylineOptions);
        } catch (ArrayIndexOutOfBoundsException e) {
            e.getLocalizedMessage();
        }
    }

    private String getEndLocationTitle(DirectionsResult results, TravelMode travelMode) {
        return StringUtils.capitalize(
                travelMode.toString()) + " Time: " + results.routes[overview].legs[overview].duration.humanReadable
                + " & Distance: " + results.routes[overview].legs[overview].distance.humanReadable;
    }

    private BitmapDescriptor getMarkerIcon(String color) {
        float[] hsv = new float[3];
        Color.colorToHSV(Color.parseColor(color), hsv);
        return BitmapDescriptorFactory.defaultMarker(hsv[0]);
    }
}
