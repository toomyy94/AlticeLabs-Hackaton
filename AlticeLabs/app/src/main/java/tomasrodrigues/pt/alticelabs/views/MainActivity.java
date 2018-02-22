package tomasrodrigues.pt.alticelabs.views;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import tomasrodrigues.pt.alticelabs.R;
import tomasrodrigues.pt.alticelabs.http.client.HttpClient;
import tomasrodrigues.pt.alticelabs.parking.ParkingPlace;
import tomasrodrigues.pt.alticelabs.utils.DateUtils;
import tomasrodrigues.pt.alticelabs.utils.HTTPUtils;
import tomasrodrigues.pt.alticelabs.utils.StringUtils;

/**
 * Launcher activity.
 * <p>
 * Created by Tomas on 12/02/2018.
 */
public class MainActivity extends AppCompatActivity {

    private static final int SECONDS_DELAYED = 1;
    ArrayList<String> availableIdsCampus = new ArrayList<>();
    ArrayList<String> availableNamesCampus = new ArrayList<>();
    ImageView parking_logo;
    public static ArrayList<ParkingPlace> parkingPlaces = new ArrayList<>();
    final Timer timer = new Timer();

    public static int numberFreePublic;
    public static int numberFreeReserved ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent initCampus = getIntent();
        if(initCampus.hasExtra(StringUtils.CAMPUS_ID_KEY)) {
            availableIdsCampus = initCampus.getStringArrayListExtra(StringUtils.CAMPUS_ID_KEY);
            availableNamesCampus = initCampus.getStringArrayListExtra(StringUtils.CAMPUS_NAME_KEY);
        }

        parking_logo = findViewById(R.id.parking_logo);
        parking_logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent showParkingMap = new Intent(v.getContext(), ParkingList.class);
                showParkingMap.putStringArrayListExtra(StringUtils.CAMPUS_ID_KEY, availableIdsCampus);
                showParkingMap.putStringArrayListExtra(StringUtils.CAMPUS_NAME_KEY, availableNamesCampus);

                timer.cancel();
                startActivityForResult(showParkingMap, 0);
            }
        });

//        updateParkingPlaces();

//        final Intent showMainActivity = new Intent(this, ParkingList.class);
//
//        new Handler().postDelayed(new Runnable() {
//            public void run() {
//                showMainActivity.putStringArrayListExtra(StringUtils.CAMPUS_ID_KEY, availableIdsCampus);
//                showMainActivity.putStringArrayListExtra(StringUtils.CAMPUS_NAME_KEY, availableNamesCampus);
//                startActivity(showMainActivity);
//
//            }
//        }, SECONDS_DELAYED * 1000);
    }

    public void updateParkingPlaces() {

        final Handler handler = new Handler(Looper.getMainLooper());

        TimerTask doAsynchronousTask = new TimerTask() {

            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            new GetAvailableParks().execute();
                            //TOP BANNER
                            //TOP BANNER
                            numberFreePublic = 0;
                            numberFreeReserved = 0;

                            for (int i = 0; i < parkingPlaces.size(); i++) {
                                if (parkingPlaces.get(i).isFree() && parkingPlaces.get(i).isReserved())
                                    numberFreeReserved++;
                                else if (parkingPlaces.get(i).isFree()) numberFreePublic++;
                            }

                            Log.d("MainActivity", "atualizei");

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
            String availableParks = client.getRequest(HTTPUtils.BASE_URL + HTTPUtils.GET_LUGARES_FROM_ZONE + availableIdsCampus.get(0));
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
