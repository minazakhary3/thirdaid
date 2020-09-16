package com.example.minazakaria.thirdaid;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
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

public class AccountActivity extends AppCompatActivity {

    private DBHandler db;
    private String baseURL;
    private ProgressDialog dialog;
    private TextView userName;
    private TextView userDName;
    private TextView userEmergReported;
    private TextView userStatus;
    private ImageView logoutButton;
    private ImageView deactButton;
    private Switch emergencyNotificationSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        dialog = new ProgressDialog(this);

        baseURL = "http://thirdaid.herokuapp.com";

        db = new DBHandler(this, null, null, 1);

        logoutButton = (ImageView)findViewById(R.id.logout_button);
        deactButton = (ImageView)findViewById(R.id.deact_button);
        userName = (TextView)findViewById(R.id.name_text);
        userDName = (TextView)findViewById(R.id.displayname_text);
        userEmergReported = (TextView)findViewById(R.id.numofemerg_text);
        userStatus = (TextView)findViewById(R.id.verified_text);
        emergencyNotificationSwitch = findViewById(R.id.getEmergSwitch);

        userName.setText(db.GetFullName());
        userDName.setText(db.GetDisplayName());
        userStatus.setText(db.GetVerifiedStatus());

        if(db.GetNotificationsStatus().equals("true")) {
            emergencyNotificationSwitch.setChecked(true);
        }

        emergencyNotificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    db.setNotificationStatus("t");
                } else {
                    db.setNotificationStatus("f");
                }
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Logout();
                    dialog.setMessage("LOGGING OUT...");
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        deactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.thirdaidapp.com/"));
                startActivity(i);
            }
        });

    }
    public void Logout () throws JSONException {

        String URL = baseURL + "/logout/client";
        final JSONObject logoutInfo = new JSONObject();

        logoutInfo.put("accId", db.GetID());
        logoutInfo.put("accToken", db.GetToken());
        logoutInfo.put("accRole", db.GetRole());

        final DBHandler db = new DBHandler(this, null, null, 1);

        JsonObjectRequest loginRequest = new JsonObjectRequest(Request.Method.POST,
                URL, logoutInfo,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getString("loginStatus").equals("0")) {
                                stopService(new Intent(AccountActivity.this, WebSocketService.class));
                                stopService(new Intent(AccountActivity.this, LocationUpdateService.class));
                                new Timer().schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        db.ClearTable();
                                        if (dialog.isShowing()) {
                                            dialog.dismiss();
                                        }
                                        Log.e("ERROR", logoutInfo.toString());
                                        GoToScreen();
                                    }
                                }, 2000);


                            } else {
                                Log.d("THING", "It's here.");
                                if (dialog.isShowing()) {
                                    dialog.dismiss();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            if (dialog.isShowing()) {
                                dialog.dismiss();
                            }
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

    }

    public void GoToScreen(){
        Intent intent = new Intent(this, SelectionActivity.class);
        startActivity(intent);
        finish();
    }

}
