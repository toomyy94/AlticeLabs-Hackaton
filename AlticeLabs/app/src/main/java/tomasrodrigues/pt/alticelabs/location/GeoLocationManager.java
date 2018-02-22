package tomasrodrigues.pt.alticelabs.location;

import android.app.Application;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

/**
 * Manager to handle the device location changes for usage with the test results.
 * <p>
 * Created by Tomás Rodrigues on 12-02-2018.
 */
public class GeoLocationManager {


    private static final int THIRTY_SECONDS = 1000 * 30;

    private static final int ONE_MINUTE = THIRTY_SECONDS * 2;

    private static final int TWO_MINUTES = ONE_MINUTE * 2;

    private static final int TEN_MINUTES = ONE_MINUTE * 10;

    private static GeoLocationManager instance;

    private final Application application;

    private final LocationListener gpsListener = new GeoLocationListener(this, LocationManager.GPS_PROVIDER);

    private final LocationListener networkListener = new GeoLocationListener(this, LocationManager.NETWORK_PROVIDER);

    private final LocationListener passiveListener = new GeoLocationListener(this, LocationManager.PASSIVE_PROVIDER);

    private Location lastKnownLocation;

    private boolean updatesEnabled = true;

    private int minTime = ONE_MINUTE;

    private float minDistance = 5.0f;

    private final Map<String, LocationListener> listeners =
            new ImmutableMap.Builder<String, LocationListener>().put(LocationManager.GPS_PROVIDER, gpsListener)
                    .put(LocationManager.NETWORK_PROVIDER, networkListener)
                    .put(LocationManager.PASSIVE_PROVIDER, passiveListener).build();

    private GeoLocationManager(Application application) {
        this.application = application;
    }

    public synchronized static GeoLocationManager getInstance(Application application) {
        if (instance == null) {
            instance = new GeoLocationManager(application);
        }
        return instance;
    }

    public void startTracking() {

        updatesEnabled = true;
        LocationManager manager = getLocationManager();

        minTime = ONE_MINUTE;
        minDistance = 5.0f;

        addProvider(manager, LocationManager.GPS_PROVIDER);
        addProvider(manager, LocationManager.NETWORK_PROVIDER);
        addProvider(manager, LocationManager.PASSIVE_PROVIDER);
    }

    public void sleepTracking() {

        LocationManager manager = getLocationManager();

        removeProvider(manager, LocationManager.GPS_PROVIDER);
        removeProvider(manager, LocationManager.NETWORK_PROVIDER);

        minTime = TEN_MINUTES;
        minDistance = 50.0f;

        addProvider(manager, LocationManager.GPS_PROVIDER);
        addProvider(manager, LocationManager.NETWORK_PROVIDER);
    }

    public void wakeTracking() {

        updatesEnabled = false;
        LocationManager manager = getLocationManager();

        removeProvider(manager, LocationManager.GPS_PROVIDER);
        removeProvider(manager, LocationManager.NETWORK_PROVIDER);

        minTime = ONE_MINUTE;
        minDistance = 5.0f;

        addProvider(manager, LocationManager.GPS_PROVIDER);
        addProvider(manager, LocationManager.NETWORK_PROVIDER);
    }

    public void stopTracking() {

        LocationManager manager = getLocationManager();

        removeProvider(manager, LocationManager.GPS_PROVIDER);
        removeProvider(manager, LocationManager.NETWORK_PROVIDER);
        removeProvider(manager, LocationManager.PASSIVE_PROVIDER);
    }

    public LocationInfo getLocationInfo() {
        if (lastKnownLocation == null) {
            lastKnownLocation = updateLastKnownLocation();
        }

        return LocationInfo.from(lastKnownLocation);
    }

    public Location getLocation() {
        if (lastKnownLocation == null) {
            lastKnownLocation = updateLastKnownLocation();
        }

        return lastKnownLocation;
    }

    void onLocationChanged(Location location) {

        if (isBetterLocation(location, lastKnownLocation)) {
            this.lastKnownLocation = location;
        }
    }

    void onProviderEnabled(String provider) {

        if (updatesEnabled) {
            addProvider(getLocationManager(), provider);
        }
    }

    void onProviderDisabled(String provider) {

        if (updatesEnabled) {
            removeProvider(getLocationManager(), provider);
        }
    }

    private LocationManager getLocationManager() {
        return (LocationManager) application.getSystemService(Context.LOCATION_SERVICE);
    }

    private void addProvider(LocationManager manager, String provider) {
        if (LocationManager.PASSIVE_PROVIDER.equals(provider)) {
            manager.requestLocationUpdates(provider, 0, 0, listeners.get(provider));
        } else if (manager.isProviderEnabled(provider)) {
            manager.requestLocationUpdates(provider, minTime, minDistance, listeners.get(provider));
        }
    }

    private void removeProvider(LocationManager manager, String provider) {
        manager.removeUpdates(listeners.get(provider));
    }

    private boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            return true;
        }

        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        if (isSignificantlyNewer) {
            return true;
        } else if (isSignificantlyOlder) {
            return false;
        }

        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        boolean isFromSameProvider = isSameProvider(location.getProvider(), currentBestLocation.getProvider());

        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    private Location updateLastKnownLocation() {
        LocationManager manager = getLocationManager();

        List<Optional<Location>> possibleLocations = getPossibleLocations(manager);
        List<Location> locations = filterExistingLocations(possibleLocations);

        Location result = new Location(LocationManager.PASSIVE_PROVIDER);
        return getBetterLocation(result, locations, locations.size() - 1);
    }

    private List<Location> filterExistingLocations(List<Optional<Location>> possibleLocations) {
        return Flowable.fromIterable(possibleLocations).filter(new LocationExistsPredicate())
                .map(new ExtractLocationFunction()).toList().blockingGet();
    }

    @NonNull
    private List<Optional<Location>> getPossibleLocations(LocationManager manager) {
        List<Optional<Location>> possibleLocations = new ArrayList<>();
        possibleLocations.add(Optional.fromNullable(manager.getLastKnownLocation(LocationManager.GPS_PROVIDER)));
        possibleLocations.add(Optional.fromNullable(manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)));
        possibleLocations.add(Optional.fromNullable(manager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)));
        return possibleLocations;
    }

    private Location getBetterLocation(Location result, List<Location> locations, int index) {
        if (index < 0 || index >= locations.size()) {
            return result;
        }

        if (isBetterLocation(locations.get(index), result)) {
            return getBetterLocation(locations.get(index), locations, index - 1);
        }
        return getBetterLocation(result, locations, index - 1);
    }

    private static class LocationExistsPredicate implements Predicate<Optional<Location>> {

        @Override
        @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
        public boolean test(@io.reactivex.annotations.NonNull Optional<Location> optional) throws Exception {
            return optional.isPresent();
        }
    }

    private static class ExtractLocationFunction implements Function<Optional<Location>, Location> {

        @Override
        @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
        public Location apply(@io.reactivex.annotations.NonNull Optional<Location> optional) throws Exception {
            return optional.orNull();
        }
    }
}
