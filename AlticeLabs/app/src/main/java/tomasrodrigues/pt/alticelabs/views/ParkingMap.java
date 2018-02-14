package tomasrodrigues.pt.alticelabs.views;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tomasrodrigues.pt.alticelabs.R;

public class ParkingMap extends FragmentActivity implements OnMapReadyCallback {

    private final static Logger LOGGER = LoggerFactory.getLogger(ParkingMap.class);

    //Fill AlticeLabs Center & bounds
    private LatLng alticeLabsCenter = new LatLng(40.6297, -8.6466);
    private LatLng alticePortoCenter = new LatLng(41.1631827,-8.6395207);
    private LatLng alticeSFRCenter = new LatLng(48.9212938, 2.3534601);
    private float radius = 0.001f;

    private GoogleMap mMap;
    private Spinner campusSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        campusSpinner =  findViewById(R.id.campus_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.campus_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        campusSpinner.setAdapter(spinnerAdapter);
        campusSpinner.setOnItemSelectedListener(new mySpinnerListener());
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

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(new LatLng(alticeLabsCenter.latitude-radius, alticeLabsCenter.longitude-radius));
        builder.include(new LatLng(alticeLabsCenter.latitude+radius, alticeLabsCenter.longitude+radius));
        LatLngBounds bounds = builder.build();

        //move camera
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(alticeLabsCenter, 19));

        //Constrain the camera target to the Altice bounds.
        mMap.setLatLngBoundsForCameraTarget(bounds);

        //No Zoom buttons
        mMap.getUiSettings().setZoomGesturesEnabled(false);
        mMap.getUiSettings().setRotateGesturesEnabled(false);

        addParkingPlaces();
    }

    public void addParkingPlaces(){
        //mMap.addMarker(new MarkerOptions().position(alticeLabsCenter).title("Marker in Altice Labs"));

    }

    private class mySpinnerListener implements AdapterView.OnItemSelectedListener {

        private final static int CAMPUS_ALTICE_LABS = 0;
        private final static int CAMPUS_POLO_PORTO = 1;
        private final static int CAMPUS_SFR = 2;

        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            LOGGER.debug("position: {}, id: {}", position, id);

            switch (position){
                case CAMPUS_ALTICE_LABS:
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(alticeLabsCenter, 19));
                    break;
                case CAMPUS_POLO_PORTO:
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(alticePortoCenter, 19));
                    break;
                case CAMPUS_SFR:
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(alticeSFRCenter, 19));
                    break;
                default:
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(alticeLabsCenter, 19));
                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
        }

    }

}
