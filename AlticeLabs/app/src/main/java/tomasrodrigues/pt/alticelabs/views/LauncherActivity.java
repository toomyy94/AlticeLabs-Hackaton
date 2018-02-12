package tomasrodrigues.pt.alticelabs.views;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import tomasrodrigues.pt.alticelabs.R;

/**
 * Launcher activity.
 * <p>
 * Created by Tomas on 12/02/2018.
 */
public class LauncherActivity extends AppCompatActivity {

    private static final int SECONDS_DELAYED = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);
        final Intent showMainActivity = new Intent(this, ParkingMap.class);

        new Handler().postDelayed(new Runnable() {
            public void run() {

                startActivity(showMainActivity);

            }
        }, SECONDS_DELAYED * 1000);
    }
}
