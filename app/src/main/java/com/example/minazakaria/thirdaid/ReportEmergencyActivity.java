package com.example.minazakaria.thirdaid;

import android.*;
import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class ReportEmergencyActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST_CODE = 20;


    private DBHandler db;

    private EditText other;

    private StorageReference storage;
    private FirebaseAuth mAuth;
    private ProgressDialog dialog;

    private  Uri photoURI;
    private String mCurrentPhotoPath;

    private String baseURL = "http://thirdaid.herokuapp.com";
    private String emergencyCode = "1";




    ArrayAdapter<CharSequence> SpinAdapterEmergencies;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_emergency);

        other = findViewById(R.id.otherText);

        ImageView uploadWithPicButton = (ImageView)findViewById(R.id.report_withpic);
        ImageView uploadWithoutPicButton = (ImageView)findViewById(R.id.report_nopic_button);

        FirebaseApp.initializeApp(this);

        db = new DBHandler(this, null, null, 1);

        spinner = (Spinner)findViewById(R.id.emergencies_spinner);


        SpinAdapterEmergencies = ArrayAdapter.createFromResource(this, R.array.emergencies, android.R.layout.simple_spinner_item);
        SpinAdapterEmergencies.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(SpinAdapterEmergencies);

        dialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                emergencyCode = String.valueOf(1 + position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        storage = FirebaseStorage.getInstance().getReference();

        if ( ContextCompat.checkSelfPermission( this, Manifest.permission.CAMERA ) != PackageManager.PERMISSION_GRANTED ) {

            ActivityCompat.requestPermissions( this, new String[] {  Manifest.permission.CAMERA  },
                    CAMERA_REQUEST_CODE );
        }

        uploadWithoutPicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    ReportEmergency(emergencyCode, other.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        uploadWithPicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(db.GetVerifiedStatus().equals("Yes")) {
                    dispatchTakePictureIntent();
                } else if(db.GetVerifiedStatus().equals("No")) {
                    Log.e("HAHA", "YOU ARE NOT VERIFIED");
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // do your stuff
        } else {
            signInAnonymously();
        }
    }

    private void signInAnonymously() {
        mAuth.signInAnonymously().addOnSuccessListener(this, new  OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                // do your stuff
            }
        })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.e("ERROR THINGY", "signInAnonymously:FAILURE", exception);
                    }
                });
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            dialog.setMessage("REPORTING EMERGENCY...");
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.show();
            final StorageReference filepath = storage.child("Photos").child(photoURI.getLastPathSegment());
            filepath.putFile(photoURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    Log.d("THING", "SUCCESS");
                    storage.child(filepath.getPath()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Log.e("URI", uri.toString());
                            try {
                                ReportEmergency(emergencyCode, other.getText().toString(), uri.toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("THING", e.getMessage());
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
            });
        }
    }

    public void ReportEmergency(String code, String description) throws JSONException {

        dialog.setMessage("REPORTING EMERGENCY...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();

        JSONObject emergencyInfo = new JSONObject();

        emergencyInfo.put("accRole", db.GetRole());
        emergencyInfo.put("accToken", db.GetToken());
        emergencyInfo.put("accId", db.GetID());
        emergencyInfo.put("pnum", db.GetPhoneNumber());
        emergencyInfo.put("fullName", db.GetFullName());
        emergencyInfo.put("emergencyCode", code);
        if(!description.equals("") && !description.equals(" ") && !description.equals(null)){
            emergencyInfo.put("emergencyDescription", description);
        } else {
            emergencyInfo.put("emergencyDescription", "No extra details were provided.");
        }
        emergencyInfo.put("lat", String.valueOf(LocationUpdateService.mLastLocation.getLatitude()));
        emergencyInfo.put("lng", String.valueOf(LocationUpdateService.mLastLocation.getLongitude()));
        emergencyInfo.put("hasImage", "0");

        String URL = baseURL + "/emergency/report";

        Log.e("JSON OBJECT:", emergencyInfo.toString());

        JsonObjectRequest loginRequest = new JsonObjectRequest(Request.Method.POST,
                URL, emergencyInfo,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.e("STATUS: ", response.getString("emergencyStatus"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {

            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(loginRequest);

        Timer tt = new Timer();
        tt.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(dialog.isShowing()){
                            dialog.hide();
                        }
                        finish();
                    }
                });

            }
        }, 1000);

        Log.e("EMERGENCY", "EMERGENCY REPORTED");

    }

    public void ReportEmergency(String code, String description, String url) throws JSONException {

        dialog.setMessage("REPORTING EMERGENCY...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        if(!dialog.isShowing()) {
            dialog.show();
        }


        JSONObject emergencyInfo = new JSONObject();

        emergencyInfo.put("accRole", db.GetRole());
        emergencyInfo.put("accToken", db.GetToken());
        emergencyInfo.put("accId", db.GetID());
        emergencyInfo.put("emergencyCode", code);
        emergencyInfo.put("pnum", db.GetPhoneNumber());
        emergencyInfo.put("fullName", db.GetFullName());
        emergencyInfo.put("lat", String.valueOf(LocationUpdateService.mLastLocation.getLatitude()));
        emergencyInfo.put("lng", String.valueOf(LocationUpdateService.mLastLocation.getLongitude()));
        emergencyInfo.put("hasImage", "1");
        emergencyInfo.put("image", url);
        if(!description.equals("") && !description.equals(" ") && !description.equals(null)){
            emergencyInfo.put("emergencyDescription", description);
        } else {
            emergencyInfo.put("emergencyDescription", "No extra details were provided.");
        }

        String URL = baseURL + "/emergency/report";

        Log.e("JSON OBJECT:", emergencyInfo.toString());

        JsonObjectRequest loginRequest = new JsonObjectRequest(Request.Method.POST,
                URL, emergencyInfo,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.e("STATUS: ", response.getString("emergencyStatus"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {

            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(loginRequest);

        Log.e("EMERGENCY", "EMERGENCY REPORTED");

        Timer tt = new Timer();
        tt.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(dialog.isShowing()){
                            dialog.hide();
                        }
                        finish();
                    }
                });

            }
        }, 1000);

    }

}
