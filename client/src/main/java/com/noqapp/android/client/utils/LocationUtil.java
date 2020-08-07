package com.noqapp.android.client.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;

import androidx.core.app.ActivityCompat;

import java.util.List;

public class LocationUtil {

    public static Location getLastKnownLoaction(boolean enabledProvidersOnly, Context context) {
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Location utilLocation = null;
        List<String> providers = manager.getProviders(enabledProvidersOnly);



            utilLocation = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if(utilLocation != null) return utilLocation;
        return null;
    }
}
