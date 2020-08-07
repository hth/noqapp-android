package com.noqapp.android.client.utils;

import com.google.common.primitives.Doubles;
import com.noqapp.android.client.presenter.beans.BizStoreElastic;
import com.noqapp.android.common.utils.GeoIP;

import java.util.Comparator;

public class SortPlaces implements Comparator<BizStoreElastic> {
    private GeoIP currentLoc;

    public SortPlaces(GeoIP current) {
        currentLoc = current;
    }

    @Override
    public int compare(final BizStoreElastic place1, final BizStoreElastic place2) {
        double lat1 = GeoHashUtils.decodeLatitude(place1.getGeoHash());
        double lon1 = GeoHashUtils.decodeLongitude(place1.getGeoHash());
        double lat2 = GeoHashUtils.decodeLatitude(place2.getGeoHash());
        double lon2 = GeoHashUtils.decodeLongitude(place2.getGeoHash());

        double distanceToPlace1 = distance(currentLoc.getLatitude(), currentLoc.getLongitude(), lat1, lon1);
        double distanceToPlace2 = distance(currentLoc.getLatitude(), currentLoc.getLongitude(), lat2, lon2);
        return Doubles.compare(distanceToPlace1, distanceToPlace2);
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = 0.0;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }
}