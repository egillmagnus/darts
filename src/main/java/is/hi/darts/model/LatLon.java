package is.hi.darts.model;

import jakarta.persistence.Embeddable;

import java.util.Locale;

@Embeddable
public class LatLon {
    private double lat;
    private double lon;

    public LatLon(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public LatLon() {
    }

    public String distance(LatLon other) {
        final double R = 6371.0;

        double lat1 = Math.toRadians(this.lat);
        double lat2 = Math.toRadians(other.lat);
        double deltaLat = Math.toRadians(other.lat - this.lat);
        double deltaLon = Math.toRadians(other.lon - this.lon);

        // Haversine formula
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                Math.cos(lat1) * Math.cos(lat2) *
                        Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distanceKm = R * c;

        if (distanceKm > 10) {
            return String.format(Locale.GERMANY, "%.1fkm", distanceKm);
        } else {
            int meters = (int) Math.round(distanceKm * 1000);
            return meters + "m";
        }
    }
}
