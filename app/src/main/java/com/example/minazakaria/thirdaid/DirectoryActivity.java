package com.example.minazakaria.thirdaid;

import android.*;
import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class DirectoryActivity extends AppCompatActivity{

    RecyclerView recyclerView;
    DirectoryAdapter adapter;

    ArrayAdapter<CharSequence> SpinAdapterArea;
    ArrayAdapter<CharSequence> SpinAdapterServices;

    Spinner spinnerArea;
    Spinner spinnerServices;


    ArrayList<DirectoryObject> currentList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directory);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CALL_PHONE},
                    1);
        }


        currentList = DirectoryListArea1();

        recyclerView = (RecyclerView)findViewById(R.id.rec_view);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setHasFixedSize(true);

        adapter = new DirectoryAdapter(this, DirectoryListArea1());
        adapter.setHasStableIds(true);
        recyclerView.setAdapter(adapter);

        spinnerArea = findViewById(R.id.areaSpinner);
        spinnerServices = findViewById(R.id.areaSpinner2);


        SpinAdapterArea = ArrayAdapter.createFromResource(this, R.array.areas, android.R.layout.simple_spinner_item);
        SpinAdapterArea.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        SpinAdapterServices = ArrayAdapter.createFromResource(this, R.array.services, android.R.layout.simple_spinner_item);
        SpinAdapterServices.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerArea.setAdapter(SpinAdapterArea);
        spinnerServices.setAdapter(SpinAdapterServices);

        spinnerArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 1) {
                    adapter = new DirectoryAdapter(getApplicationContext(), DirectoryListArea1());
                    recyclerView.setAdapter(adapter);
                    currentList = DirectoryListArea1();
                    spinnerServices.setSelection(0);
                } else {
                    adapter = new DirectoryAdapter(getApplicationContext(), DirectoryListArea2());
                    recyclerView.setAdapter(adapter);
                    currentList = DirectoryListArea2();
                    spinnerServices.setSelection(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        spinnerServices.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                adapter = new DirectoryAdapter(getApplicationContext(), ListMaker(currentList, String.valueOf(position), "0", false));
                recyclerView.setAdapter(adapter);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



    }

    private ArrayList<DirectoryObject> DirectoryListArea1 (){

        ArrayList<DirectoryObject> directoryObjects = new ArrayList<>();

        directoryObjects.add(new DirectoryPersonal("Dr. Douglas G. Brooks, MD", "Family Medicine, General Practice.", "+000 0000 0000", "01277705343", "general"));
        directoryObjects.add(new DirectoryPersonal("Matthew A. Brown, DO", "Adolescent Medicine, General Practice.", "+000 0000 0000", "01277705343", "general"));
        directoryObjects.add(new DirectoryPersonal("Dr. Meedeum Cho", "General Practice.", "+000 0000 0000", "01277705343", "general"));

        directoryObjects.add(new DirectoryPersonal("Dr. Douglas G. Brooks, MD", "Dermatology.", "+000 0000 0000", "01277705343", "dermatology"));
        directoryObjects.add(new DirectoryPersonal("Dr. Mariam Gad", "Dermatology.", "+000 0000 0000", "01277705343", "dermatology"));
        directoryObjects.add(new DirectoryPersonal("Dr. Youssef Safwat", "Dermatology.", "+000 0000 0000", "01277705343", "dermatology"));


        directoryObjects.add(new DirectoryHospital("Cleopatra Hospital", "Private Hospital.", "+000 0000 0000", "01277705343"));
        directoryObjects.add(new DirectoryHospital("Egypt Railway Hospital", "Public Hospital.", "+000 0000 0000", "01277705343"));

        directoryObjects.add(new DirectoryPharmacy("Seif Pharmacy", "Open 24 hours.", "+000 0000 0000", "01277705343"));
        directoryObjects.add(new DirectoryPharmacy("El Ezaby Pharmacy", "Open 24 hours.", "+000 0000 0000", "01277705343"));

        return directoryObjects;

    }

    private ArrayList<DirectoryObject> DirectoryListArea2 (){

        ArrayList<DirectoryObject> directoryObjects = new ArrayList<>();

        directoryObjects.add(new DirectoryPersonal("Dr. Seif Tabouzadah", "Family Medicine, General Practice.", "+000 0000 0000", "01277705343", "general"));
        directoryObjects.add(new DirectoryPersonal("Dr. Mariam Madbouly", "Adolescent Medicine, General Practice.", "+000 0000 0000", "01277705343", "general"));
        directoryObjects.add(new DirectoryPersonal("Omar El Shemerly, MD", "General Practice.", "+000 0000 0000", "01277705343", "general"));

        directoryObjects.add(new DirectoryPersonal("Dr. Youssef Kamal", "Dermatology.", "+000 0000 0000", "01277705343", "dermatology"));
        directoryObjects.add(new DirectoryPersonal("Dr. Marwan Samir", "Dermatology.", "+000 0000 0000", "01277705343", "dermatology"));
        directoryObjects.add(new DirectoryPersonal("Dr. Mina Zakaria", "Dermatology.", "+000 0000 0000", "01277705343", "dermatology"));


        directoryObjects.add(new DirectoryHospital("Ains Shams Hospital", "Public Hospital.", "+000 0000 0000", "01277705343"));
        directoryObjects.add(new DirectoryHospital("Qasr Al Nile Hospital", "Public Hospital.", "+000 0000 0000", "01277705343"));

        directoryObjects.add(new DirectoryPharmacy("Dr. Rania Pharmacy", "Open 24 hours.", "+000 0000 0000", "01277705343"));
        directoryObjects.add(new DirectoryPharmacy("New Moon Pharmacy", "Open 24 hours.", "+000 0000 0000", "01277705343"));

        return directoryObjects;

    }

    public ArrayList<DirectoryObject> ListMaker(ArrayList<DirectoryObject> objectsList, String id, String category, boolean Phys){

        ArrayList<DirectoryObject> output = new ArrayList<>();

        if(Phys) {

            if(category.equals("0")){
                for (DirectoryObject object : objectsList) {
                    if (object.getId().equals("1")) {
                        output.add(object);
                    }
                }
                return output;
            } else if(category.equals("general")) {
                for (DirectoryObject object : objectsList) {
                    if (object.getCategory().equals("general")) {
                        output.add(object);
                    }
                }
                return output;
            } else if(category.equals("dermatology")) {
                for (DirectoryObject object : objectsList) {
                    if (object.getCategory().equals("dermatology")) {
                        output.add(object);
                    }
                }
                return output;
            }

        }

        if (id.equals("0")){

            return objectsList;

        } else if (id.equals("1")){
            if(category.equals("0")) {
                for (DirectoryObject object : objectsList) {
                    if (object.getId().equals("1")) {
                        output.add(object);
                    }
                }

                return output;
            }


        } else if (id.equals("2")) {


            for (DirectoryObject object : objectsList) {
                if (object.getId().equals("2")) {
                    output.add(object);
                }
            }

            return output;
        } else if (id.equals("3")) {


            for (DirectoryObject object : objectsList) {
                if (object.getId().equals("3")) {
                    output.add(object);
                }
            }

            return output;
        }

        return objectsList;

    }


}


















