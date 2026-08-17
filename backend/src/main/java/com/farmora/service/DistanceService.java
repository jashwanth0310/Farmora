package com.farmora.service;

import org.springframework.stereotype.Service;

/**
 * Distance Finder
 * ----------------
 * Computes great-circle distance between two lat/lng points using the
 * Haversine formula. Used whenever a buyer searches for farmers/produce,
 * so every result can show "X km away" and an estimated delivery time,
 * and results can be sorted/filtered by distance.
 */
@Service
public class DistanceService {

    private static final double EARTH_RADIUS_KM = 6371.0;

    /**
     * @return distance in kilometers between two coordinates, rounded to 1 decimal.
     * Returns null if either point is missing (so callers can fall back gracefully).
     */
    public Double distanceKm(Double lat1, Double lon1, Double lat2, Double lon2) {
        if (lat1 == null || lon1 == null || lat2 == null || lon2 == null) {
            return null;
        }
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = EARTH_RADIUS_KM * c;

        return Math.round(distance * 10.0) / 10.0;
    }

    /**
     * Rough estimated travel time assuming an average rural/urban mixed-route
     * speed of 30 km/h for a mini-truck. Purely for prototype/demo purposes.
     */
    public String estimatedTravelTime(Double distanceKm) {
        if (distanceKm == null) return "N/A";
        double avgSpeedKmph = 30.0;
        double hours = distanceKm / avgSpeedKmph;
        int minutes = (int) Math.round(hours * 60);
        if (minutes < 60) {
            return minutes + " min";
        }
        int h = minutes / 60;
        int m = minutes % 60;
        return h + "h " + (m > 0 ? m + "m" : "");
    }

    /**
     * Simple distance-based transport cost estimate for the logistics module.
     * Base fare + per-km rate, purely illustrative for the prototype.
     */
    public double estimateTransportCost(double totalDistanceKm) {
        double baseFare = 300.0;
        double perKmRate = 18.0;
        return Math.round(baseFare + (totalDistanceKm * perKmRate));
    }

    /**
     * Normalized 0-1 "distance score" used by the matching engine -
     * closer farmers score higher. Anything beyond 100km tapers to 0.
     */
    public double distanceScore(Double distanceKm) {
        if (distanceKm == null) return 0.5; // unknown location, neutral score
        double maxRelevantDistance = 100.0;
        if (distanceKm >= maxRelevantDistance) return 0.0;
        return 1.0 - (distanceKm / maxRelevantDistance);
    }
}
