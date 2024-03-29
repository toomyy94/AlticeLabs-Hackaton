package tomasrodrigues.pt.alticelabs.views;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import tomasrodrigues.pt.alticelabs.R;
import tomasrodrigues.pt.alticelabs.http.client.HttpClient;
import tomasrodrigues.pt.alticelabs.parking.ParkingPlace;
import tomasrodrigues.pt.alticelabs.utils.HTTPUtils;
import tomasrodrigues.pt.alticelabs.utils.StringUtils;

/**
 * Launcher activity.
 * <p>
 * Created by Tomas on 12/02/2018.
 */
public class LauncherActivity extends AppCompatActivity {

    private static final int SECONDS_DELAYED = 1;
    ArrayList<String> availableIdsCampus = new ArrayList<>();
    ArrayList<String> availableNamesCampus = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new GetAvailableCampus().execute();

        final Intent showMainActivity = new Intent(this, MainActivity.class);

        new Handler().postDelayed(new Runnable() {
            public void run() {
                showMainActivity.putStringArrayListExtra(StringUtils.CAMPUS_ID_KEY, availableIdsCampus);
                showMainActivity.putStringArrayListExtra(StringUtils.CAMPUS_NAME_KEY, availableNamesCampus);
                startActivity(showMainActivity);

            }
        }, SECONDS_DELAYED * 1500);
    }

    public class GetAvailableCampus extends AsyncTask {

        @Override
        protected Object doInBackground(Object... arg0) {
            HttpClient client = new HttpClient();
            String availableCampus = client.getRequest(HTTPUtils.BASE_URL + HTTPUtils.GET_ZONES);
            Log.d("availableCampus", availableCampus);
            try{
                JSONObject total_records = new JSONObject(availableCampus);
                JSONArray records = total_records.getJSONArray("records");

                for (int i = 0 ; i < records.length() ; i++){
                    JSONObject record = records.getJSONObject(i);

                    availableIdsCampus.add(record.getString("id"));
                    availableNamesCampus.add(record.getString("name"));
                }

                Log.d("Parsing ids with sucess", availableIdsCampus.get(0));

                new GetAvailableParks().execute();

            }catch (Exception e){
                Log.e("Error", "Error on parsing campus");
                e.getMessage();
            }

            return null;
        }
    }

    public class GetAvailableParks extends AsyncTask {

        @Override
        protected Object doInBackground(Object... arg0) {
            HttpClient client = new HttpClient();
            String availableParks = client.getRequest(HTTPUtils.BASE_URL + HTTPUtils.GET_LUGARES_FROM_ZONE + availableIdsCampus.get(0));
            Log.d("availableParks", availableParks);
            try{
                //para a lista
                ParkingList.parkingPlaces.clear();

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

                    ParkingList.parkingPlaces.add(pPlace);
                }

            }catch (Exception e){
                Log.e("Error", "Parsing parks"+ e.getMessage());
                SystemClock.sleep(500);
                new GetAvailableParks().execute();

            }

            return null;
        }
    }

}
