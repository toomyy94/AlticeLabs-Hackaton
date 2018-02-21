package tomasrodrigues.pt.alticelabs.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tomasrodrigues.pt.alticelabs.R;
import tomasrodrigues.pt.alticelabs.parking.ParkingPlace;

public class ParkingMap extends FragmentActivity implements OnMapReadyCallback {

    private final static Logger LOGGER = LoggerFactory.getLogger(ParkingMap.class);
    public final static int CAMERA_ZOOM = 19;
    public final static int NUMBER_ALL_PLACES = 50;
    public final static boolean RESERVED = true;
    public final static boolean PUBLIC = false;

    //Fill AlticeLabs Center & bounds
    public static final LatLng alticeLabsCenter = new LatLng(40.6297, -8.6466);
    public static final LatLng alticePortoCenter = new LatLng(41.1631827,-8.6395207);
    public static final LatLng alticeSFRCenter = new LatLng(48.9212938, 2.3534601);
    public static final float radius = 0.001f;

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

        addInitialParkingPlaces();

        //No Zoom buttons
        mMap.getUiSettings().setZoomGesturesEnabled(false);
        mMap.getUiSettings().setRotateGesturesEnabled(false);
    }

    public void goToCampus(LatLng campus){
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(new LatLng(campus.latitude-radius, campus.longitude-radius));
        builder.include(new LatLng(campus.latitude+radius, campus.longitude+radius));
        LatLngBounds campusBounds = builder.build();

        //Constrain the camera bounds.
        mMap.setLatLngBoundsForCameraTarget(campusBounds);

        //move camera
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(campus, CAMERA_ZOOM));
    }

    private class mySpinnerListener implements AdapterView.OnItemSelectedListener {

        private final static int CAMPUS_ALTICE_LABS = 0;
        private final static int CAMPUS_POLO_PORTO = 1;
        private final static int CAMPUS_SFR = 2;

        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            LOGGER.debug("position: {}, id: {}", position, id);

            switch (position){
                case CAMPUS_ALTICE_LABS:
                    goToCampus(alticeLabsCenter);
                    break;
                case CAMPUS_POLO_PORTO:
                    goToCampus(alticePortoCenter);
                    break;
                case CAMPUS_SFR:
                    goToCampus(alticeSFRCenter);
                    break;
                default:
                    goToCampus(alticeLabsCenter);
                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
        }

    }

    public void addInitialParkingPlaces(){
        for(int i = 0; i < alticeLabsParkingPlaces.length ; i++) {
            mMap.addMarker(new MarkerOptions()
                .position(alticeLabsParkingPlaces[i].getGeo())
                    .icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.car_red_icon_horizontal))
                        .rotation(alticeLabsParkingPlaces[i].getRotation()) );
        }
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    ParkingPlace[] alticeLabsParkingPlaces = {
            //frente do edificio 0
            new ParkingPlace(new LatLng(40.629144, -8.646928), RESERVED, -7),
            new ParkingPlace(new LatLng(40.629168, -8.646922), RESERVED, -7),
            new ParkingPlace(new LatLng(40.629191, -8.646924), RESERVED, -7),
            new ParkingPlace(new LatLng(40.629213, -8.646927), RESERVED, -7),
            new ParkingPlace(new LatLng(40.629236, -8.646933), RESERVED, -7),
            new ParkingPlace(new LatLng(40.629259, -8.646934), RESERVED, -7),
            new ParkingPlace(new LatLng(40.629281, -8.646938), RESERVED, -7),
            new ParkingPlace(new LatLng(40.629302, -8.646941), RESERVED, -7),
            new ParkingPlace(new LatLng(40.629324, -8.646947), RESERVED, -7),
            new ParkingPlace(new LatLng(40.629346, -8.646950), RESERVED, -7),
            new ParkingPlace(new LatLng(40.629369, -8.646953), RESERVED, -7),
            new ParkingPlace(new LatLng(40.629389, -8.646953), RESERVED, -7),
            new ParkingPlace(new LatLng(40.629413, -8.646954), RESERVED, -7),
            new ParkingPlace(new LatLng(40.629434, -8.646961), RESERVED, -7),
            new ParkingPlace(new LatLng(40.629458, -8.646961), RESERVED, -7),
            new ParkingPlace(new LatLng(40.629479, -8.646965), RESERVED, -7),
            new ParkingPlace(new LatLng(40.629469, -8.646839), RESERVED, -7),
            new ParkingPlace(new LatLng(40.629447, -8.646831), RESERVED, -7),
            new ParkingPlace(new LatLng(40.629424, -8.646828), RESERVED, -7),
            new ParkingPlace(new LatLng(40.629403, -8.646823), RESERVED, -7),
            new ParkingPlace(new LatLng(40.629376, -8.646817), RESERVED, -7),
            new ParkingPlace(new LatLng(40.629357, -8.646816), RESERVED, -7),
            new ParkingPlace(new LatLng(40.629336, -8.646808), RESERVED, -7),
            new ParkingPlace(new LatLng(40.629313, -8.646805), RESERVED, -7),
            new ParkingPlace(new LatLng(40.629292, -8.646803), RESERVED, -7),
            new ParkingPlace(new LatLng(40.629269, -8.646799), RESERVED, -7),
            new ParkingPlace(new LatLng(40.629246, -8.646796), RESERVED, -7),
            new ParkingPlace(new LatLng(40.629225, -8.646792), RESERVED, -7),
            new ParkingPlace(new LatLng(40.629203, -8.646789), RESERVED, -7),
            new ParkingPlace(new LatLng(40.629181, -8.646785), RESERVED, -7),
            new ParkingPlace(new LatLng(40.629158, -8.646777), RESERVED, -7),

            //em frente do jardim em frente ao edificio 0
            //TODO: ...
    };

}
