package com.example.minazakaria.thirdaid;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class NearbyEmergenciesActivity extends AppCompatActivity {

    private ImageView viewMapButton;

    RecyclerView recyclerView;
    EmergencyAdapter adapter;
    EmergencyAdapter adapterNear;

    Spinner locationSpinner;

    ArrayList<EmergencyObject> nearbyEmergencies;
    ArrayList<EmergencyObject> emergencyList2;
    ArrayAdapter<CharSequence> SpinAdapterArea;

    boolean near = true;

    TextView nothing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_emergencies);

        nothing = findViewById(R.id.textView6);

        viewMapButton = (ImageView)findViewById(R.id.view_map_button);

        viewMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoToMap();
            }
        });

        locationSpinner = findViewById(R.id.locationSpinner);

        SpinAdapterArea = ArrayAdapter.createFromResource(this, R.array.emergencyLocations, android.R.layout.simple_spinner_item);
        SpinAdapterArea.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        locationSpinner.setAdapter(SpinAdapterArea);

        emergencyList2 = new ArrayList<>();
        nearbyEmergencies = new ArrayList<>();

        recyclerView = findViewById(R.id.emergencyRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new EmergencyAdapter(this, emergencyList2);
        adapterNear = new EmergencyAdapter(this, nearbyEmergencies);
        recyclerView.setAdapter(adapterNear);

        recyclerView.setVisibility(View.INVISIBLE);

        locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    recyclerView.setAdapter(adapterNear);
                    near = true;
                } else {
                    recyclerView.setAdapter(adapter);
                    near = false;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Timer tt = new Timer();
        tt.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(WebSocketService.emergencyList.size() > 0) {

                        }
                        emergencyList2.clear();
                        nearbyEmergencies.clear();
                        if(WebSocketService.emergencyList.size() == 0){
                            recyclerView.setVisibility(View.INVISIBLE);
                            nothing.setVisibility(View.VISIBLE);
                        } else {
                            recyclerView.setVisibility(View.VISIBLE);
                            nothing.setVisibility(View.INVISIBLE);
                            for(EmergencyObject emergency : WebSocketService.emergencyList) {
                                emergencyList2.add(0, emergency);
                                if(emergency.distance < 100) {
                                    nearbyEmergencies.add(0, emergency);
                                }
                                if(near) {
                                    adapterNear = new EmergencyAdapter(getApplicationContext(), nearbyEmergencies);
                                    recyclerView.setAdapter(adapterNear);
                                } else {
                                    adapter = new EmergencyAdapter(getApplicationContext(), emergencyList2);
                                    recyclerView.setAdapter(adapter);
                                }

                            }
                        }
                    }
                });

            }
        }, 0, 5 * 1000);
    }

    public void GoToMap(){
        Intent intent = new Intent(this, LiveMapActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if(getIntent().getStringExtra("id") != null) {
            Intent intent = new Intent(this, HomescreenActivity.class);
            startActivity(intent);
            finish();
        }
        super.onBackPressed();
    }
}
