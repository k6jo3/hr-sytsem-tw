package com.company.hrms.attendance.domain.service;

import org.springframework.stereotype.Service;

@Service
public class LocationValidationDomainService {

    public boolean isLocationValid(double lat, double lon, double siteLat, double siteLon, double radiusMeters) {
        // Simple distance calculation (Haversine formula approximation or separate
        // util)
        // For now, simpler implementation
        double earthRadius = 6371e3; // meters
        double dLat = Math.toRadians(siteLat - lat);
        double dLon = Math.toRadians(siteLon - lon);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat)) * Math.cos(Math.toRadians(siteLat)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = earthRadius * c;

        return distance <= radiusMeters;
    }
}
