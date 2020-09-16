package com.example.minazakaria.thirdaid;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class LocationUpdateService extends Service {


    private static final long INTERVAL = 1000 * 5;
    private static final long FASTEST_INTERVAL = 1000 * 1;

    LocationRequest mLocationRequest;
    public static Location mLastLocation;

    public static double speed;

    FusedLocationProviderClient mFusedLocationClient;
    LocationCallback mLocationCallback;

    public static boolean isActive = false;

    protected void creatLocationRequest(){
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void createLocationCallBack(){
        mLocationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for(Location location : locationResult.getLocations()) {
                    mLastLocation = location;
                    speed = location.getSpeed();
                    Log.e("LOCATION CHANGED", location.toString());
                    Log.e("SPEED CHANGED", String.valueOf(location.getSpeed()));
                }
            }
        };
    }

    @Override
    public IBinder onBind(Intent arg0)
    {
        return null;
    }

    @SuppressLint("MissingPermission")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        super.onStartCommand(intent, flags, startId);

        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());

        isActive = true;

        Log.e("STARTED", "STARTED");

        return START_STICKY;
    }

    @Override
    public void onCreate()
    {
        creatLocationRequest();
        createLocationCallBack();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

    }

    @Override
    public void onDestroy()
    {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        Log.e("LOCATION SERVICE", "DESTROYED");

        isActive = false;

    }

}



