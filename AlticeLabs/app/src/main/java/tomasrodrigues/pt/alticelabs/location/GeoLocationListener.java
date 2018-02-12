package tomasrodrigues.pt.alticelabs.location;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

/**
 * Helper class to avoid implement methods that are not being used.
 * <p>
 * Created by Tomás Rodrigues on 12-02-2018.
 */
class GeoLocationListener implements LocationListener {

    private GeoLocationManager manager;

    private String provider;

    GeoLocationListener(GeoLocationManager manager, String provider) {
        this.manager = manager;
        this.provider = provider;
    }

    @Override
    public void onLocationChanged(Location location) {
        manager.onLocationChanged(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        if (this.provider.equals(provider)) {
            manager.onProviderEnabled(provider);
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        if (this.provider.equals(provider)) {
            manager.onProviderDisabled(provider);
        }
    }
}
