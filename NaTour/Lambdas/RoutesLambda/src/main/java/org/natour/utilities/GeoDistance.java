package org.natour.utilities;

import org.natour.entities.LatLng;

public class GeoDistance {

    private static double rad(double x) {
        return x * Math.PI / 180;
    }


    public static double haversineDistance(LatLng p1, LatLng p2) {

        double EarthRadius = 6378137.0;
        double dLat = rad(p2.getLatitude() - p1.getLatitude());
        double dLong = rad(p2.getLongitude() - p1.getLongitude());
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(rad(p1.getLatitude())) * Math.cos(rad(p2.getLatitude())) *
                        Math.sin(dLong / 2) * Math.sin(dLong / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EarthRadius * c;
    }
}
