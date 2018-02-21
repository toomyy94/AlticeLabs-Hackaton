package tomasrodrigues.pt.alticelabs.parking;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Tomás Rodrigues on 14/02/2018.
 */

public class ParkingPlace {

    private static int nextId = 0;
    private int id;
    private LatLng geo;
    private boolean isReserved;
    private int rotation;

    public ParkingPlace(LatLng geo, boolean isReserved, int rotation) {
        this.id = nextId;
        nextId++;
        this.geo = geo;
        this.isReserved = isReserved;
        this.rotation = rotation;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
}
