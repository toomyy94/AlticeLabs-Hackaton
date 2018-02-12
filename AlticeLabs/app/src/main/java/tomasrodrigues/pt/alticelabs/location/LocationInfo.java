package tomasrodrigues.pt.alticelabs.location;

import android.location.Location;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Contains information about a Geo Location.
 * Created by Tomás Rodrigues on 12-02-2018.
 */
public class LocationInfo {

    private static final double MS_TO_MIH_RATIO = 2.23693629;

    private double latitude;

    private double longitude;

    private double speed;

    private double bearing;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getSpeed() {
        return speed;
    }

    void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getBearing() {
        return bearing;
    }

    void setBearing(double bearing) {
        this.bearing = bearing;
    }

    public String format() {
        StringBuilder builder = new StringBuilder();
        DecimalFormat coordinateFormatter = coordinateFormatter();
        DecimalFormat decimalFormat = numberFormatter();

        formatLatitude(builder, coordinateFormatter, latitude);
        builder.append(" ");
        formatLongitude(builder, coordinateFormatter, longitude);
        builder.append(" ").append(decimalFormat.format(speed)).append(" ").append(decimalFormat.format(bearing));

        return builder.toString();
    }

    public static LocationInfo format(String gpsLocation) {

        String[] gpsLocationParts = gpsLocation.split(" ");

        LocationInfo locationInfo = new LocationInfo();

        locationInfo.setLatitude(Double.parseDouble(gpsLocationParts[0]));

        if (gpsLocationParts[2].equals("S")) {
            locationInfo.setLatitude(locationInfo.getLatitude() * -1);
        }

        locationInfo.setLongitude(Double.parseDouble(gpsLocationParts[2]));

        if (gpsLocationParts[4].equals("W")) {
            locationInfo.setLongitude(locationInfo.getLongitude() * -1);
        }

        locationInfo.setSpeed(Double.parseDouble(gpsLocationParts[4]));
        locationInfo.setBearing(Double.parseDouble(gpsLocationParts[5]));

        return locationInfo;
    }

    public static LocationInfo from(Location location) {
        LocationInfo info = new LocationInfo();
        if (location == null) {
            return info;
        }
        info.setLatitude(location.getLatitude());
        info.setLongitude(location.getLongitude());
        info.setSpeed(location.getSpeed() * MS_TO_MIH_RATIO);
        info.setBearing((double) location.getBearing());
        return info;
    }

    private void formatLatitude(StringBuilder builder, DecimalFormat formatter, double value) {
        String formattedValue = convert(formatter, value);
        String direction = value >= 0 ? "N" : "S";
        builder.append(formattedValue).append(" ").append(direction);
    }

    private void formatLongitude(StringBuilder builder, DecimalFormat formatter, double value) {
        String formattedValue = convert(formatter, value);
        String direction = value >= 0 ? "E" : "W";
        builder.append(formattedValue).append(" ").append(direction);
    }

    private String convert(DecimalFormat formatter, double value) {
        if (value < 0) {
            value = -value;
        }
        int degrees = (int) Math.floor(value);
        double minutes = (value - degrees) * 60;
        return degrees + formatter.format(minutes);
    }

    private DecimalFormat numberFormatter() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.ENGLISH);
        return new DecimalFormat("0.000000", symbols);
    }

    private DecimalFormat coordinateFormatter() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.ENGLISH);
        return new DecimalFormat("###.#####", symbols);
    }

    @Override
    public String toString() {
        return "LocationInfo{" + "latitude=" + latitude + ", longitude=" + longitude + ", speed=" + speed +
                ", bearing=" + bearing + '}';
    }
}
