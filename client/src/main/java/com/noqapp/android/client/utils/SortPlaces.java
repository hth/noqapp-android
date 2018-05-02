package com.noqapp.android.client.utils;

import com.google.android.gms.maps.model.LatLng;
import com.noqapp.android.client.presenter.beans.BizStoreElastic;

import java.util.Comparator;

public class SortPlaces implements Comparator<BizStoreElastic> {
    LatLng currentLoc;

    public SortPlaces(LatLng current){
        currentLoc = current;
    }
    @Override
    public int compare(final BizStoreElastic place1, final BizStoreElastic place2) {


        double lat1 = GeoHashUtils.decodeLatitude(place1.getGeoHash());
        double lon1 = GeoHashUtils.decodeLongitude(place1.getGeoHash());
        double lat2 = GeoHashUtils.decodeLatitude(place2.getGeoHash());
        double lon2 = GeoHashUtils.decodeLongitude(place2.getGeoHash());

        double distanceToPlace1 = distance(currentLoc.latitude, currentLoc.longitude, lat1, lon1);
        double distanceToPlace2 = distance(currentLoc.latitude, currentLoc.longitude, lat2, lon2);
        return (int) (distanceToPlace1 - distanceToPlace2);
    }

    public double distance(double fromLat, double fromLon, double toLat, double toLon) {
        double radius = 6378137;   // approximate Earth radius, *in meters*
        double deltaLat = toLat - fromLat;
        double deltaLon = toLon - fromLon;
        double angle = 2 * Math.asin( Math.sqrt(
                Math.pow(Math.sin(deltaLat/2), 2) +
                        Math.cos(fromLat) * Math.cos(toLat) *
                                Math.pow(Math.sin(deltaLon/2), 2) ) );
        return radius * angle;
    }
}