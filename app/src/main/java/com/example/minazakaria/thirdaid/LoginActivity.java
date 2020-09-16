package com.example.minazakaria.thirdaid;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    //Variables
    private ImageView loginButton;
    private  EditText username;
    private  EditText password;
    private TextView errorText;

    private ProgressDialog dialog;

    private String baseURL;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dialog = new ProgressDialog(this);

        baseURL = "http://thirdaid.herokuapp.com";

        loginButton = (ImageView)findViewById(R.id.login_button);
        username = (EditText)findViewById(R.id.username_input);
        password = (EditText)findViewById(R.id.password_input);
        errorText = (TextView)findViewById(R.id.error_text);

        final DBHandler db = new DBHandler(this, null, null, 1);

        if(db.Count() >= 1) {
            GoToHomeScreen();
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    dialog.setMessage("LOGGING IN...");
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.setCancelable(false);
                    dialog.show();

                    Login(username.getText().toString(), password.getText().toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });


    }

    public void Login(String username, String password) throws JSONException {

        String URL = baseURL + "/login";
        JSONObject loginInfo = new JSONObject();

        loginInfo.put("username", username);
        loginInfo.put("password", password);
        loginInfo.put("platform", "client");

        final DBHandler db = new DBHandler(this, null, null, 1);

        JsonObjectRequest loginRequest = new JsonObjectRequest(Request.Method.POST,
                URL, loginInfo,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            if(response.getString("loginStatus").equals("1")){
                                if(response.getString("accRole").equals("user")){
                                    User user = new User(response.getJSONObject("accInfo").getString("id"), response.getJSONObject("accInfo").getString("firstName"),
                                            response.getJSONObject("accInfo").getString("lastName"), response.getString("accToken"), response.getJSONObject("accInfo").getString("displayName"),
                                            response.getString("accRole"),  response.getJSONObject("accInfo").getString("verified"),
                                            "f", "t", response.getJSONObject("accInfo").getString("pnum"));
                                    db.AddUser(user);
                                    CreateSession();
                                } else {
                                    if (dialog.isShowing()) {
                                        dialog.dismiss();
                                    }
                                    errorText.setText("Your account cannot login to the app.");
                                }

                            } else if(response.getString("loginStatus").equals("0")) {
                                if (dialog.isShowing()) {
                                    dialog.dismiss();
                                }
                                AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(LoginActivity.this);
                                dlgAlert.setMessage("Wrong username or password.");
                                dlgAlert.setTitle("Login Error");
                                dlgAlert.setCancelable(false);
                                dlgAlert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                });

                                dlgAlert.create().show();
                            } else if(response.getString("loginStatus").equals("2")) {
                                if (dialog.isShowing()) {
                                    dialog.dismiss();
                                }
                                AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(LoginActivity.this);
                                dlgAlert.setMessage("You appear to be logged in from another phone. You cannot be logged in from two phones at once.");
                                dlgAlert.setTitle("Multiple Devices");
                                dlgAlert.setCancelable(false);
                                dlgAlert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                });

                                dlgAlert.create().show();
                            }else{
                                if (dialog.isShowing()) {
                                    dialog.dismiss();
                                }
                                AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(LoginActivity.this);
                                dlgAlert.setMessage("Something went wrong!");
                                dlgAlert.setTitle("Error");
                                dlgAlert.setCancelable(false);
                                dlgAlert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                });

                                dlgAlert.create().show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                errorText.setText( "Nope");
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

    public void CreateSession() throws JSONException {
        String URL = baseURL + "/createSession/client";
        JSONObject sessionInfo = new JSONObject();

        final DBHandler db = new DBHandler(this, null, null, 1);

        sessionInfo.put("accId", db.GetID());
        sessionInfo.put("accToken", db.GetToken());
        sessionInfo.put("accRole", db.GetRole());

        Log.e("JSON ERROR", db.GetID());
        Log.e("JSON ERROR", sessionInfo.toString());


        JsonObjectRequest sessionRequest = new JsonObjectRequest(Request.Method.POST,
                URL, sessionInfo,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            if(response.getString("SessionCreated").equals("true")){
                                if (dialog.isShowing()) {
                                    dialog.dismiss();
                                    StartServices();
                                    GoToHomeScreen();
                                }
                            } else {
                                if (dialog.isShowing()) {
                                    dialog.dismiss();
                                }
                                errorText.setText("Something has gone wrong!");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                errorText.setText("Nope");
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
        requestQueue.add(sessionRequest);
    }

    public void GoToHomeScreen(){
        Intent intent = new Intent(this, HomescreenActivity.class);
        startActivity(intent);
        finish();
    }

    public void StartServices (){
        Intent location = new Intent(this, LocationUpdateService.class);
        Intent socket = new Intent(this, WebSocketService.class);

        startService(location);
        startService(socket);
    }

}
