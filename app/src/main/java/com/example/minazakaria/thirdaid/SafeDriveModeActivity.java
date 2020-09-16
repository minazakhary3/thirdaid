package com.example.minazakaria.thirdaid;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class SafeDriveModeActivity extends AppCompatActivity {

    private boolean status;
    private boolean responded = false;
    private boolean isSafe = false;

    private String baseURL = "http://thirdaid.herokuapp.com";

    private double currentSpeed;
    private double lastSpeed;

    private Switch safeDriveSwitch;
    private TextView safeDriveStatusText;

    private DBHandler db;
    private ProgressDialog dialog;

    private TextView speedChangeText;
    private TextView estSpeedText;
    private TextView lastSpeedText;

    private Button simulateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safe_drive_mode);

        db = new DBHandler(this, null, null, 1);
        dialog = new ProgressDialog(this);

        safeDriveSwitch = (Switch)findViewById(R.id.safeDriveSwitch);
        safeDriveStatusText = (TextView)findViewById(R.id.safeDriveStatus);

        speedChangeText = (TextView)findViewById(R.id.speedChange);
        estSpeedText = (TextView)findViewById(R.id.speedEst);
        lastSpeedText = (TextView)findViewById(R.id.lastSpeed);

        simulateButton = (Button)findViewById(R.id.simulate_button);

        simulateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckIfUserIsSafe();
            }
        });

        safeDriveSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked) {
                    AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(SafeDriveModeActivity.this);
                    dlgAlert.setMessage("You are about to turn Safe Drive Mode on. Please do not close the app or lock your phone for Safe Drive to work properly." +
                            " (Note: Safe Drive works best at high cruising speeds.)");
                    dlgAlert.setTitle("Safe Drive Mode");
                    dlgAlert.setCancelable(false);
                    dlgAlert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            safeDriveStatusText.setText("Safe Drive Mode: ON");
                            status = true;
                        }
                    });
                    dlgAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            safeDriveSwitch.setChecked(false);
                            status = false;
                        }
                    });
                    dlgAlert.create().show();
                } else {
                    safeDriveStatusText.setText("Safe Drive Mode: OFF");
                    status = false;
                }
            }
        });

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(status) {
                            CheckSpeed();
                        }
                    }
                });
            }
        }, 0, 1000 * 5);



    }

    public void CheckSpeed(){
        double speedChange;
        double temp;

        temp = currentSpeed;
        currentSpeed = LocationUpdateService.speed * 3.6;
        lastSpeed = temp;


        speedChange =lastSpeed - currentSpeed;

        if(speedChange > 40 && currentSpeed < 10) {
            CheckIfUserIsSafe();
        }

        speedChangeText.setText("Speed Change: " + speedChange);
        estSpeedText.setText("Estimated Speed: " + currentSpeed + " KM/H");
        lastSpeedText.setText("Last Speed: " + lastSpeed + " KM/H");


    }

    public void CheckIfUserIsSafe(){
        responded = false;

        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(SafeDriveModeActivity.this);
        dlgAlert.setMessage("Your stopped suddenly. Are you safe? \n If you don't respond to this message in 30 seconds, an emergency is going to be reported at your current location.");
        dlgAlert.setTitle("Sudden Speed Decrease");
        dlgAlert.setCancelable(false);
        dlgAlert.setPositiveButton("Yes, I am safe", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                responded = true;
                isSafe = true;
            }
        });
        dlgAlert.setNegativeButton("No, I am not safe", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                responded = true;
                isSafe = false;
                    status = false;
                    if(!isSafe) {
                        safeDriveSwitch.setChecked(false);
                        safeDriveStatusText.setText("EMERGENCY REPORTED\nAT CURRENT LOCATION");
                        ReportPossibleCrash();
                        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            v.vibrate(VibrationEffect.createOneShot(3000, VibrationEffect.DEFAULT_AMPLITUDE));
                        }else{
                            v.vibrate(2500);
                        }
                    }

            }
        });
        final AlertDialog dialog = dlgAlert.create();
        dialog.show();

        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(3000, VibrationEffect.DEFAULT_AMPLITUDE));
        }else{
            v.vibrate(2500);
        }
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if(dialog.isShowing()){
                    dialog.dismiss();
                    status = false;
                    if(!isSafe && !responded) {
                        safeDriveSwitch.setChecked(false);
                        safeDriveStatusText.setText("EMERGENCY REPORTED\nAT CURRENT LOCATION");
                        ReportPossibleCrash();
                        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            v.vibrate(VibrationEffect.createOneShot(3000, VibrationEffect.DEFAULT_AMPLITUDE));
                        }else{
                            v.vibrate(2500);
                        }
                    }

                }
            }
        };

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                handler.removeCallbacks(runnable);
            }
        });

        handler.postDelayed(runnable, 10 *1000);
    }

    public void ReportPossibleCrash(){

        Log.e("EMERGENCY", "EMERGENCY REPORTED AT " + LocationUpdateService.mLastLocation.getLongitude() + ", " + LocationUpdateService.mLastLocation.getLatitude());
        try {
            ReportEmergency("50", "Safe Drive");
        } catch (JSONException e) {
            e.printStackTrace();
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
        emergencyInfo.put("emergencyCode", code);
        emergencyInfo.put("emergencyDescription", "A potential accident");
        emergencyInfo.put("pnum", db.GetPhoneNumber());
        emergencyInfo.put("lat", String.valueOf(LocationUpdateService.mLastLocation.getLatitude()));
        emergencyInfo.put("lng", String.valueOf(LocationUpdateService.mLastLocation.getLongitude()));
        emergencyInfo.put("fullName", db.GetFullName());
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

    @Override
    public void onBackPressed(){
        if(status) {
            AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(SafeDriveModeActivity.this);
            dlgAlert.setMessage("Safe Drive is currently on. To go back to the app, turn Safe Drive off.");
            dlgAlert.setCancelable(false);
            dlgAlert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            dlgAlert.create().show();

        } else {
            super.onBackPressed();
        }
    }
}
