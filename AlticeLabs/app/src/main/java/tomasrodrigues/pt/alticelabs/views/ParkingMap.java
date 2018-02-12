package tomasrodrigues.pt.alticelabs.views;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;

import tomasrodrigues.pt.alticelabs.R;

public class ParkingMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        LatLng alticeLabsCenter = new LatLng(40.6297, -8.6466);
        float radius = 0.001f;

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(new LatLng(alticeLabsCenter.latitude-radius, alticeLabsCenter.longitude-radius));
        builder.include(new LatLng(alticeLabsCenter.latitude+radius, alticeLabsCenter.longitude+radius));
        LatLngBounds bounds = builder.build();

//        mMap.addMarker(new MarkerOptions().position(alticeLabsCenter).title("Marker in Altice Labs"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(alticeLabsCenter, 19));

        // Constrain the camera target to the Altice bounds.
        mMap.setLatLngBoundsForCameraTarget(bounds);

        //No Zoom buttons
        mMap.getUiSettings().setZoomGesturesEnabled(false);
        mMap.getUiSettings().setRotateGesturesEnabled(false);
    }

}
