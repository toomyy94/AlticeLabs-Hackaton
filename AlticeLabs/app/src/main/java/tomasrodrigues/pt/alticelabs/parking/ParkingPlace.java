package tomasrodrigues.pt.alticelabs.parking;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Tomás Rodrigues on 14/02/2018.
 */

public class ParkingPlace {

    private static int nextId = 0;
    private int id;
    private LatLng geo;
    private boolean isFree;
    private boolean isReserved;
    private int rotation;
    private long timestamp;

    public ParkingPlace() {
        this.id = nextId;
        nextId++;
    }

    public ParkingPlace(LatLng geo, boolean isFree, boolean isReserved, int rotation, long timestamp) {
        this.id = nextId;
        nextId++;
        this.geo = geo;
        this.isFree = isFree;
        this.isReserved = isReserved;
        this.rotation = rotation;
        this.timestamp = timestamp;
    }

    public ParkingPlace(LatLng geo, boolean isFree, boolean isReserved, int rotation) {
        this.id = nextId;
        nextId++;
        this.geo = geo;
        this.isFree = isFree;
        this.isReserved = isReserved;
        this.rotation = rotation;
    }

    public LatLng getGeo() {
        return geo;
    }

    public void setGeo(LatLng geo) {
        this.geo = geo;
    }

    public boolean isReserved() {
        return isReserved;
    }

    public void setReserved(boolean reserved) {
        isReserved = reserved;
    }

    public int getRotation() {
        return rotation;
    }

    public void setRotation(int rotation) {
        this.rotation = rotation;
    }

    public boolean isFree() {
        return isFree;
    }

    public void setFree(boolean free) {
        isFree = free;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
