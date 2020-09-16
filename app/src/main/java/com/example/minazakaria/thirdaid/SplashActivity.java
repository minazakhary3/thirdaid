package com.example.minazakaria.thirdaid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        final DBHandler db = new DBHandler(this, null, null, 1);



        if(db.Count() > 0) {
            GoToHomeScreen();
        }

        else {GoToSelection();
        }



    }

    public void GoToSelection(){
        Intent intent = new Intent(this, SelectionActivity.class);
        startActivity(intent);
        finish();
    }

    public void GoToHomeScreen(){
        Intent intent = new Intent(this, HomescreenActivity.class);
        startActivity(intent);
        finish();
    }
}
