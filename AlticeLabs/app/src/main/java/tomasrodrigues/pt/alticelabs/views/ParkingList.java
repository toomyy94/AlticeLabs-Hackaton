package tomasrodrigues.pt.alticelabs.views;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.support.design.widget.Snackbar;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import tomasrodrigues.pt.alticelabs.R;
import tomasrodrigues.pt.alticelabs.http.client.HttpClient;
import tomasrodrigues.pt.alticelabs.parking.ParkingListAdapter;
import tomasrodrigues.pt.alticelabs.parking.ParkingPlace;
import tomasrodrigues.pt.alticelabs.utils.DateUtils;
import tomasrodrigues.pt.alticelabs.utils.HTTPUtils;
import tomasrodrigues.pt.alticelabs.utils.StringUtils;

public class ParkingList extends FragmentActivity {

    public final static int CAMPUS_ALTICE_LABS = 0;
    public final static int CAMPUS_POLO_PORTO = 1;
    public final static int CAMPUS_SFR = 2;

    ArrayList<String> availableIdsCampus = new ArrayList<>();
    ArrayList<String> availableNamesCampus = new ArrayList<>();
    long lastTimestampPark = 0L;

//    ParkingPlace[] parkingPlaces = ParkingMap.alticeLabsParkingPlaces;
    public static ArrayList<ParkingPlace> parkingPlaces = new ArrayList<>();
    private ParkingListAdapter adapter;
    ListView listView;

    Spinner campusSpinner;
    public static TextView text_numberFreePublic;
    public static TextView text_numberFreeReserved;
    public static ImageView car_icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_list);

        text_numberFreePublic = findViewById(R.id.number_free_public);
        text_numberFreeReserved = findViewById(R.id.number_free_reserved);
        car_icon = findViewById(R.id.item_icon);
        campusSpinner =  findViewById(R.id.campus_spinner);
        listView = findViewById(R.id.available_parks);

        Intent initCampus = getIntent();
        if(initCampus.hasExtra(StringUtils.CAMPUS_ID_KEY)){
            availableIdsCampus = initCampus.getStringArrayListExtra(StringUtils.CAMPUS_ID_KEY);
            availableNamesCampus = initCampus.getStringArrayListExtra(StringUtils.CAMPUS_NAME_KEY);
        }
        else if(initCampus.hasExtra(StringUtils.SELECTED_SPINNER_CAMPUS_KEY)){
            availableNamesCampus = initCampus.getStringArrayListExtra(StringUtils.SELECTED_SPINNER_CAMPUS_KEY);
        }

        Intent intent = getIntent();
        if (intent.hasExtra(StringUtils.SELECTED_SPINNER_CAMPUS_KEY)) {
            campusSpinner.setSelection(intent.getIntExtra(StringUtils.SELECTED_SPINNER_CAMPUS_KEY, 0));
        }

        //Campus
        // Create an ArrayAdapter using the string array and a default spinner layout
//        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
//                R.array.campus_array, android.R.layout.simple_spinner_item);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<> (getApplicationContext(), android.R.layout.simple_spinner_item, availableNamesCampus);
        // Specify the layout to use when the list of choices appears
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        campusSpinner.setAdapter(spinnerAdapter);
        campusSpinner.setOnItemSelectedListener(new mySpinnerListener());

        //List
        adapter = new ParkingListAdapter(parkingPlaces, getApplicationContext());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final ParkingPlace parkingPlace = parkingPlaces.get(position);

                Snackbar snackbar = Snackbar.make(view, ""+parkingPlace.getGeo(), Snackbar.LENGTH_LONG)
                        .setActionTextColor(Color.GREEN)
                        .setAction("Ver no mapa!", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                final Intent showParkingMap = new Intent(view.getContext(), ParkingMap.class);

                                showParkingMap.putStringArrayListExtra(StringUtils.CAMPUS_ID_KEY, availableIdsCampus);
                                showParkingMap.putStringArrayListExtra(StringUtils.CAMPUS_NAME_KEY, availableNamesCampus);
                                showParkingMap.putExtra(StringUtils.SELECTED_SPINNER_CAMPUS_KEY, campusSpinner.getSelectedItemPosition());
                                showParkingMap.putExtra(StringUtils.LAT_KEY, parkingPlace.getGeo().latitude);
                                showParkingMap.putExtra(StringUtils.LNG_KEY, parkingPlace.getGeo().longitude);

                                startActivity(showParkingMap);
                            }
                        });

                snackbar.show();
            }
        });

        updateParkingPlaces();

        //Floating button
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent showParkingMap = new Intent(getApplicationContext(), ParkingMap.class);
                showParkingMap.putExtra(StringUtils.SELECTED_SPINNER_CAMPUS_KEY, campusSpinner.getSelectedItemPosition());
                showParkingMap.putStringArrayListExtra(StringUtils.CAMPUS_ID_KEY, availableIdsCampus);
                showParkingMap.putStringArrayListExtra(StringUtils.CAMPUS_NAME_KEY, availableNamesCampus);
                startActivity(showParkingMap);
            }
        });
    }
    
    private class mySpinnerListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            Log.d("item selecionado", "item selecionado");

            switch (position){
                case CAMPUS_ALTICE_LABS:
                    break;
                case CAMPUS_POLO_PORTO:
                    break;
                case CAMPUS_SFR:
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
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

    public void updateParkingPlaces() {

        final Handler handler = new Handler(Looper.getMainLooper());
        Timer timer = new Timer();

        TimerTask doAsynchronousTask = new TimerTask() {

            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            new GetAvailableParks().execute();
                            //TOP BANNER
                            int numberFreePublic = 0;
                            int numberFreeReserved = 0;
                            for (int i = 0; i < parkingPlaces.size(); i++) {
                                if (parkingPlaces.get(i).isFree() && parkingPlaces.get(i).isReserved())
                                    numberFreeReserved++;
                                else if (parkingPlaces.get(i).isFree()) numberFreePublic++;
                            }

                            text_numberFreePublic.setText(String.valueOf(numberFreePublic));
                            text_numberFreeReserved.setText(String.valueOf(numberFreeReserved));

                            //LIST
                            adapter.notifyDataSetChanged();

                            Log.d("atualizei", "atualizei");
                        } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            });
        }};
        timer.schedule(doAsynchronousTask, 0,
                DateUtils.SECONDS_PERIOD * DateUtils.MILI_TO_SECOND); //execute in every xxxxx ms
    }


    public class GetAvailableParks extends AsyncTask {

        @Override
        protected Object doInBackground(Object... arg0) {
            HttpClient client = new HttpClient();
            String availableParks = client.getRequest(HTTPUtils.BASE_URL + HTTPUtils.GET_LUGARES_FROM_ZONE + availableIdsCampus.get(campusSpinner.getSelectedItemPosition()));
            Log.d("availableParks", availableParks);
            try{
                //para a lista
                parkingPlaces.clear();

                JSONObject total_records = new JSONObject(availableParks);
                JSONArray records = total_records.getJSONArray("records");

                for (int i = 0 ; i < records.length() ; i++){
                    JSONObject record = records.getJSONObject(i);

                    ParkingPlace pPlace = new ParkingPlace();

                    if (record.getString("category").equals("PUBLIC")) pPlace.setReserved(false);
                    else pPlace.setReserved(true);

                    JSONObject layer = record.getJSONObject("layer");
                    pPlace.setGeo(new LatLng(layer.getDouble("y"), layer.getDouble("x")));
                    pPlace.setRotation(layer.getInt("rotation"));

                    if (record.has("currLog")) {
                        JSONObject currLog = record.getJSONObject("currLog");
                        pPlace.setTimestamp(currLog.getLong("from"));

                        if (currLog.getString("status").equals("FREE")) pPlace.setFree(true);
                        else pPlace.setFree(false);
                    }
                    else pPlace.setFree(false);

                    parkingPlaces.add(pPlace);
                }

            }catch (Exception e){
                Log.e("Error", "Parsing parks"+ e.getMessage());

            }

            return null;
        }
    }

}
