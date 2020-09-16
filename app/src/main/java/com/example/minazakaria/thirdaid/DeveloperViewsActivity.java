package com.example.minazakaria.thirdaid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Timer;
import java.util.TimerTask;

public class DeveloperViewsActivity extends AppCompatActivity {

    private TextView locationText;

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer_views);

        final DBHandler db = new DBHandler(this, null, null, 1);

        Button startS = (Button)findViewById(R.id.startService);
        Button stopS = (Button)findViewById(R.id.stopService);

        locationText = (TextView)findViewById(R.id.locationText);


        startS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.setCertifiedStatus("t");
            }
        });

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(LocationUpdateService.mLastLocation != null){
                            locationText.setText("Location: " + String.valueOf(LocationUpdateService.mLastLocation.getLongitude()) + ", " + String.valueOf(LocationUpdateService.mLastLocation.getLatitude()));
                        }
                    }
                });
            }
        }, 0, 1000);



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
