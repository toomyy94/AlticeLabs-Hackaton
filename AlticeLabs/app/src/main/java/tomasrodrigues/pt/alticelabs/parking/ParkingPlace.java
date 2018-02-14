package tomasrodrigues.pt.alticelabs.parking;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Tomás Rodrigues on 14/02/2018.
 */

public class ParkingPlace {

    private int id;
    private LatLng geo;
    private boolean isReserved;

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
}
