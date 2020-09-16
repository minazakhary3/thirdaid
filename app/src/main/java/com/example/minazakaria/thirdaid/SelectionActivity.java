package com.example.minazakaria.thirdaid;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class SelectionActivity extends AppCompatActivity {

    public static boolean isActive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);

        //Button Images
        ImageView loginButton = (ImageView)findViewById(R.id.login_button_selection);
        ImageView signupButton = (ImageView)findViewById(R.id.signup_button_selection);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoToLoginScreen();
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoToSignupPage();
            }
        });

        if(HomescreenActivity.act != null) {
            HomescreenActivity.act.finish();
        }


    }

    public void GoToLoginScreen(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("RESUME", "METHOD HAS BEEN CALLED");
    }

    public void GoToSignupPage(){
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.thirdaidapp.com/signup"));
        startActivity(i);
    }

    @Override
    protected void onStart() {
        super.onStart();
        isActive = true;
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        isActive = false;
    }
}
