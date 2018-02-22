package tomasrodrigues.pt.alticelabs.views;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import tomasrodrigues.pt.alticelabs.R;
import tomasrodrigues.pt.alticelabs.parking.ParkingPlace;
import tomasrodrigues.pt.alticelabs.utils.StringUtils;

public class ParkingMap extends FragmentActivity implements OnMapReadyCallback {

    public final static int CAMERA_ZOOM = 19;
    public final static int NUMBER_ALL_PLACES = 50;
    public static StringUtils st = new StringUtils();

    ArrayList<String> availableIdsCampus = new ArrayList<>();
    ArrayList<String> availableNamesCampus = new ArrayList<>();

    //Fill AlticeLabs Center & bounds
    public static final LatLng alticeLabsCenter = new LatLng(40.629789, -8.646458);
    public static final LatLng alticePortoCenter = new LatLng(41.1631827,-8.6395207);
    public static final LatLng alticeSFRCenter = new LatLng(48.9212938, 2.3534601);
    public static final float radius = 0.001f;

    private GoogleMap mMap;
    private Spinner campusSpinner;
    private TextView text_numberFreePublic;
    private TextView text_numberReservedPublic;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_map);

        text_numberFreePublic = findViewById(R.id.number_free_public);
        text_numberReservedPublic = findViewById(R.id.number_free_reserved);

        text_numberFreePublic.setText(ParkingList.text_numberFreePublic.getText());
        text_numberReservedPublic.setText(ParkingList.text_numberFreeReserved.getText());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Gets
        Intent initCampus = getIntent();
        if(initCampus.hasExtra(StringUtils.CAMPUS_NAME_KEY)){
            availableIdsCampus = initCampus.getStringArrayListExtra(StringUtils.CAMPUS_ID_KEY);
            availableNamesCampus = initCampus.getStringArrayListExtra(StringUtils.CAMPUS_NAME_KEY);
        }

        //Floating button
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent showParkingList = new Intent(getApplicationContext(), ParkingList.class);
                showParkingList.putExtra(StringUtils.SELECTED_SPINNER_CAMPUS_KEY, campusSpinner.getSelectedItemPosition());
                showParkingList.putStringArrayListExtra(StringUtils.CAMPUS_ID_KEY, availableIdsCampus);
                showParkingList.putStringArrayListExtra(StringUtils.CAMPUS_NAME_KEY, availableNamesCampus);
                startActivity(showParkingList);
            }
        });

        campusSpinner =  findViewById(R.id.campus_spinner);

        // Create an ArrayAdapter using the string array and a default spinner layout
//        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
//                R.array.campus_array, android.R.layout.simple_spinner_item);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, availableNamesCampus);

        // Specify the layout to use when the list of choices appears
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        campusSpinner.setAdapter(spinnerAdapter);
        campusSpinner.setOnItemSelectedListener(new mySpinnerListener());

        Intent intent = getIntent();
        if (intent.hasExtra(StringUtils.SELECTED_SPINNER_CAMPUS_KEY)) {
            campusSpinner.setSelection(intent.getIntExtra(StringUtils.SELECTED_SPINNER_CAMPUS_KEY, 0));
        }
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

        //Constrain the camera bounds.
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(new LatLng(campus.latitude - radius, campus.longitude - radius));
        builder.include(new LatLng(campus.latitude + radius, campus.longitude + radius));
        LatLngBounds campusBounds = builder.build();
        mMap.setLatLngBoundsForCameraTarget(campusBounds);

        //If is comming from the List row
        Intent intent = getIntent();
        if (intent.hasExtra(StringUtils.LAT_KEY)) {
            LatLng latLng = new LatLng(intent.getDoubleExtra(StringUtils.LAT_KEY, 0), intent.getDoubleExtra(StringUtils.LNG_KEY, 0));

            //move camera
            Log.d("Moving camera to marker",""+latLng.toString());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, CAMERA_ZOOM));
        }
        else{
            //move camera
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(campus, CAMERA_ZOOM));
        }

    }

    public float distance(float lat_a, float lng_a, float lat_b, float lng_b )
    {
        double earthRadius = 3958.75;
        double latDiff = Math.toRadians(lat_b-lat_a);
        double lngDiff = Math.toRadians(lng_b-lng_a);
        double a = Math.sin(latDiff /2) * Math.sin(latDiff /2) +
                Math.cos(Math.toRadians(lat_a)) * Math.cos(Math.toRadians(lat_b)) *
                        Math.sin(lngDiff /2) * Math.sin(lngDiff /2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double distance = earthRadius * c;

        int meterConversion = 1609;

        return new Float(distance * meterConversion).floatValue();
    }

    private class mySpinnerListener implements AdapterView.OnItemSelectedListener {

        private final static int CAMPUS_ALTICE_LABS = 0;
        private final static int CAMPUS_POLO_PORTO = 1;
        private final static int CAMPUS_SFR = 2;

        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Log.d("Item selecionado", ""+position);
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
            Log.d("Nada selecionado", "Nada selecionado");

        }

    }

    public void addInitialParkingPlaces(){
        for(int i = 0; i < ParkingList.parkingPlaces.size() ; i++) {
            mMap.addMarker(new MarkerOptions()
                .position(ParkingList.parkingPlaces.get(i).getGeo())
                    .icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.car_red_icon_horizontal))
                        .rotation(ParkingList.parkingPlaces.get(i).getRotation()) );
        }
    }

    public boolean isCampusCenter(LatLng center){
        if (center == alticeLabsCenter || center == alticePortoCenter || center == alticeSFRCenter) return true;
        else return false;
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public static ParkingPlace[] alticeLabsParkingPlaces = {
            //frente do edificio 0
            new ParkingPlace(new LatLng(40.629144, -8.646928), st.OCCUPIED, st.RESERVED, -7),
            new ParkingPlace(new LatLng(40.629168, -8.646922), st.OCCUPIED, st.RESERVED, -7),
            new ParkingPlace(new LatLng(40.629191, -8.646924), st.OCCUPIED, st.RESERVED, -7),
            new ParkingPlace(new LatLng(40.629213, -8.646927), st.OCCUPIED, st.RESERVED, -7),
            new ParkingPlace(new LatLng(40.629236, -8.646933), st.OCCUPIED, st.RESERVED, -7),
            new ParkingPlace(new LatLng(40.629259, -8.646934), st.OCCUPIED, st.RESERVED, -7),
            new ParkingPlace(new LatLng(40.629281, -8.646938), st.OCCUPIED, st.RESERVED, -7),
            new ParkingPlace(new LatLng(40.629302, -8.646941), st.OCCUPIED, st.RESERVED, -7),
            new ParkingPlace(new LatLng(40.629324, -8.646947), st.OCCUPIED, st.RESERVED, -7),
            new ParkingPlace(new LatLng(40.629346, -8.646950), st.OCCUPIED, st.RESERVED, -7),
            new ParkingPlace(new LatLng(40.629369, -8.646953), st.OCCUPIED, st.RESERVED, -7),
            new ParkingPlace(new LatLng(40.629389, -8.646953), st.OCCUPIED, st.RESERVED, -7),
            new ParkingPlace(new LatLng(40.629413, -8.646954), st.OCCUPIED, st.RESERVED, -7),
            new ParkingPlace(new LatLng(40.629434, -8.646961), st.OCCUPIED, st.RESERVED, -7),
            new ParkingPlace(new LatLng(40.629458, -8.646961), st.OCCUPIED, st.RESERVED, -7),
            new ParkingPlace(new LatLng(40.629479, -8.646965), st.OCCUPIED, st.RESERVED, -7),
            new ParkingPlace(new LatLng(40.629469, -8.646839), st.OCCUPIED, st.RESERVED, -7),
            new ParkingPlace(new LatLng(40.629447, -8.646831), st.OCCUPIED, st.RESERVED, -7),
            new ParkingPlace(new LatLng(40.629424, -8.646828), st.OCCUPIED, st.RESERVED, -7),
            new ParkingPlace(new LatLng(40.629403, -8.646823), st.OCCUPIED, st.RESERVED, -7),
            new ParkingPlace(new LatLng(40.629376, -8.646817), st.OCCUPIED, st.RESERVED, -7),
            new ParkingPlace(new LatLng(40.629357, -8.646816), st.OCCUPIED, st.RESERVED, -7),
            new ParkingPlace(new LatLng(40.629336, -8.646808), st.OCCUPIED, st.RESERVED, -7),
            new ParkingPlace(new LatLng(40.629313, -8.646805), st.OCCUPIED, st.RESERVED, -7),
            new ParkingPlace(new LatLng(40.629292, -8.646803), st.OCCUPIED, st.RESERVED, -7),
            new ParkingPlace(new LatLng(40.629269, -8.646799), st.OCCUPIED, st.RESERVED, -7),
            new ParkingPlace(new LatLng(40.629246, -8.646796), st.OCCUPIED, st.RESERVED, -7),
            new ParkingPlace(new LatLng(40.629225, -8.646792), st.OCCUPIED, st.RESERVED, -7),
            new ParkingPlace(new LatLng(40.629203, -8.646789), st.OCCUPIED, st.RESERVED, -7),
            new ParkingPlace(new LatLng(40.629181, -8.646785), st.OCCUPIED, st.RESERVED, -7),
            new ParkingPlace(new LatLng(40.629158, -8.646777), st.OCCUPIED, st.RESERVED, -7),

            //em frente do jardim em frente ao edificio 0
            //TODO: ...
    };

//    public void updateParkingPlaces() {
//
//        final Handler handler = new Handler();
//        Timer timer = new Timer();
//
//        TimerTask doAsynchronousTask = new TimerTask() {
//            @Override
//            public void run() {
//                handler.post(new Runnable() {
//                    public void run() {
//                        try {
//                            new GetAvailableParks().execute();
//                            //TOP BANNER
//                            int numberFreePublic = 0;
//                            int numberFreeReserved = 0;
//                            for (int i = 0; i < parkingPlaces.size(); i++) {
//                                if (parkingPlaces.get(i).isFree() && parkingPlaces.get(i).isReserved())
//                                    numberFreeReserved++;
//                                else if (parkingPlaces.get(i).isFree()) numberFreePublic++;
//                            }
//                            text_numberFreePublic.setText(String.valueOf(numberFreePublic));
//                            text_numberReservedPublic.setText(String.valueOf(numberFreeReserved));
//
//                            //LIST
//                            adapter = new ParkingListAdapter(parkingPlaces, getApplicationContext());
//                            listView.setAdapter(adapter);
//
//                            Log.d("atualizei", "atualizei");
//                        } catch (Exception e) {
//                            // TODO Auto-generated catch block
//                            e.printStackTrace();
//                        }
//                    }
//                });
//            }
//        };
//        timer.schedule(doAsynchronousTask, 0,
//                DateUtils.SECONDS_PERIOD * DateUtils.MILI_TO_SECOND); //execute in every xxxxx ms
//    }
//
//    public class GetAvailableParks extends AsyncTask {
//
//        @Override
//        protected Object doInBackground(Object... arg0) {
//            HttpClient client = new HttpClient();
//            String availableParks = client.getRequest(HTTPUtils.BASE_URL + HTTPUtils.GET_LUGARES_FROM_ZONE + availableIdsCampus.get(campusSpinner.getSelectedItemPosition()));
//            Log.d("availableParks", availableParks);
//            try{
//                //para a lista
//                parkingPlaces.clear();
//
//                JSONObject total_records = new JSONObject(availableParks);
//                JSONArray records = total_records.getJSONArray("records");
//
//                for (int i = 0 ; i < records.length() ; i++){
//                    JSONObject record = records.getJSONObject(i);
//
//                    ParkingPlace pPlace = new ParkingPlace();
//
//                    if (record.getString("category").equals("PUBLIC")) pPlace.setReserved(false);
//                    else pPlace.setReserved(true);
//
//                    JSONObject layer = record.getJSONObject("layer");
//                    pPlace.setGeo(new LatLng(layer.getDouble("y"), layer.getDouble("x")));
//                    pPlace.setRotation(layer.getInt("rotation"));
//
//                    if (record.has("currLog")) {
//                        JSONObject currLog = record.getJSONObject("currLog");
//                        pPlace.setTimestamp(currLog.getLong("from"));
//
//                        if (currLog.getString("status").equals("FREE")) pPlace.setFree(true);
//                        else pPlace.setFree(false);
//                    }
//                    else pPlace.setFree(false);
//
//                    parkingPlaces.add(pPlace);
//                }
//
//            }catch (Exception e){
//                Log.e("Error", "Parsing parks"+ e.getMessage());
//
//            }
//
//            return null;
//        }
//    }

}
