package com.example.minazakaria.thirdaid;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class LiveMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DBHandler db;


    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        db = new DBHandler(this, null, null, 1);


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

    }


    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setTiltGesturesEnabled(false);
        LatLng loc = new LatLng(LocationUpdateService.mLastLocation.getLatitude(), LocationUpdateService.mLastLocation.getLongitude());


        // Add a marker in Sydney and move the camera
        mMap.setMyLocationEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 15.0f));

        LatLng point = new LatLng(30, 30);

        int height = 40;
        int width = 40;
        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.marker_dot);
        Bitmap b=bitmapdraw.getBitmap();
        final Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);

        BitmapDrawable bitmapdraw2=(BitmapDrawable)getResources().getDrawable(R.drawable.marker_dot_emergency);
        Bitmap b2=bitmapdraw2.getBitmap();
        final Bitmap smallMarker2 = Bitmap.createScaledBitmap(b2, width, height, false);

        BitmapDrawable bitmapdraw3=(BitmapDrawable)getResources().getDrawable(R.drawable.marker_dot_hospital);
        Bitmap b3=bitmapdraw3.getBitmap();
        final Bitmap smallMarker3 = Bitmap.createScaledBitmap(b3, width, height, false);

        Timer tt = new Timer();
        tt.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mMap.clear();
                        for (ConnectedUser user : WebSocketService.usersList) {
                            Marker marker = mMap.addMarker(new MarkerOptions()
                                    .position(user.getCurrentLocation())
                                    .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
                        }
                        for (EmergencyObject emergency : WebSocketService.emergencyList) {
                            Marker marker = mMap.addMarker(new MarkerOptions()
                                    .position(emergency.getLocation())
                                    .title(emergency.type + ", " + emergency.description)
                                    .icon(BitmapDescriptorFactory.fromBitmap(smallMarker2)));
                        }
                        for (HospitalObject hospital : WebSocketService.hospitalsList) {
                            Marker marker = mMap.addMarker(new MarkerOptions()
                                    .position(hospital.location)
                                    .icon(BitmapDescriptorFactory.fromBitmap(smallMarker3)));
                        }
                    }
                });
            }
        }, 0, 5000);



    }
}
