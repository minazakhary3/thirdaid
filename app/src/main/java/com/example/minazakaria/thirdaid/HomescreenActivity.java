package com.example.minazakaria.thirdaid;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class HomescreenActivity extends AppCompatActivity {

    private DBHandler db;
    private String baseURL;
    private ProgressDialog dialog;

    public static Activity act;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homescreen);
        dialog = new ProgressDialog(this);
        act = this;

        if (!LocationUpdateService.isActive || !WebSocketService.isActive) {
            dialog.setMessage("CONNECTING");
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.show();
            startService(new Intent(this, WebSocketService.class));
            startService(new Intent(this, LocationUpdateService.class));
            Timer tt = new Timer();
            tt.schedule(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.hide();
                        }
                    });
                }
            }, 2000);
        }



        //Object Assignment

        baseURL = "http://thirdaid.herokuapp.com";
        db = new DBHandler(this, null, null, 1);

        runtime_permissions();

        //Button Images
        ImageView ReportEmergencyButton = (ImageView)findViewById(R.id.report_emerg_button);
        ImageView AccountButton = (ImageView)findViewById(R.id.account_button);
        ImageView DirectoryButton = (ImageView)findViewById(R.id.directory_button);
        ImageView SafeDriveButton = (ImageView)findViewById(R.id.safe_drive_button);
        ImageView NearbyButton = (ImageView)findViewById(R.id.nearby_button);

        //Text Views
        TextView tokenView = (TextView)findViewById(R.id.token_text);

        tokenView.setText(db.GetToken());

        //Click Listeners
        ReportEmergencyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoToReport();
            }
        });

        AccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoToAccount();
            }
        });

        DirectoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoToDirectory();
            }
        });

        SafeDriveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoToSafeDrive();
            }
        });

        NearbyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoToNearbyEmergencies();
            }
        });

        tokenView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                GoToDevView();
                return true;
            }
        });

    }

    public void GoToDevView(){
        Intent intent = new Intent(this, DeveloperViewsActivity.class);
        startActivity(intent);
    }

    public void GoToReport(){
        Intent intent = new Intent (this, ReportEmergencyActivity.class);
        startActivity(intent);
    }

    public void GoToAccount(){
        Intent intent = new Intent (this, AccountActivity.class);
        startActivity(intent);
    }

    public void GoToSafeDrive(){
        Intent intent = new Intent (this, SafeDriveModeActivity.class);
        startActivity(intent);
    }

    public void GoToNearbyEmergencies(){
        Intent intent = new Intent (this, NearbyEmergenciesActivity.class);
        startActivity(intent);
    }

    public void GoToDirectory(){
        Intent intent = new Intent (this, DirectoryActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed(){
        moveTaskToBack(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public boolean runtime_permissions(){
        if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 100) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){

            } else {
                runtime_permissions();
            }
        }
    }
}
