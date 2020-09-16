package com.example.minazakaria.thirdaid;

import android.*;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

public class emergencyViewActivity extends AppCompatActivity {

    ImageView callReporter;

    TextView Type;
    TextView Description;
    TextView Reporter;
    TextView Time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_view);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.CALL_PHONE},
                    1);
        }

        Type = findViewById(R.id.emergTypeText);
        Reporter = findViewById(R.id.textView5);
        Description = findViewById(R.id.textView4);
        callReporter = findViewById(R.id.callReporterButton);
        Time = findViewById(R.id.textView3);

        Type.setText(getIntent().getStringExtra("type"));
        Reporter.setText("Reported By: " + getIntent().getStringExtra("reporter"));
        Description.setText("Other Details: " + getIntent().getStringExtra("details"));
        Time.setText("Reported At: " + getIntent().getStringExtra("time"));



        callReporter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse("tel:" + intent.getStringExtra("pnum")));
                Log.e("INTENT", "THIS IS WORKING");
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                getApplicationContext().startActivity(intent);
            }
        });

    }
}
