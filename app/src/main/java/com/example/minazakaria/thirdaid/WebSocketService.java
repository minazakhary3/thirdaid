package com.example.minazakaria.thirdaid;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class WebSocketService extends Service {

    private static DBHandler db;
    private TimerTask tt;

    public static ArrayList<ConnectedUser> usersList;
    public static ArrayList<EmergencyObject> emergencyList;
    public static ArrayList<HospitalObject> hospitalsList;

    public static int numberOfUsers = 0;

    public static boolean isActive = false;
    private boolean added=false;

    private static Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://thirdaid.herokuapp.com/server/users");
        } catch (URISyntaxException e) {}
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        usersList = new ArrayList<>();
        emergencyList = new ArrayList<>();
        hospitalsList = new ArrayList<>();

        isActive = true;

        if(mSocket != null && !mSocket.connected()){
            mSocket.connect();
            Log.e("SOCKET", "SENT CONNECTION");
        }

        mSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.e("HELLO", "CONNECTED");
                AddUser();
            }
        });



        mSocket.on("emergency reported", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0];
                try {
                    if (!data.getJSONObject("reporter").getString("accId").equals(db.GetID()) && db.GetNotificationsStatus().equals("true")) {
                        try {
                            makeNotification(data.getString("id"), idToType(data.getString("code")));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        mSocket.on("user list update", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.e("Update", "List updated");
                JSONArray data = (JSONArray) args[0];
                Log.e("ARRAY: ", data.toString());
                usersList.clear();
                numberOfUsers = data.length();
                for (int i = 0; i < data.length(); i++) {
                    try {
                        if(!data.getJSONObject(i).getString("accId").equals(db.GetID())){
                            usersList.add(new ConnectedUser(new LatLng(data.getJSONObject(i).getDouble("lat"), data.getJSONObject(i).getDouble("lng")),
                                    data.getJSONObject(i).getString("accId")));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

        mSocket.on("emergency list update", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.e("Update", "Emergencies updated");
                JSONArray data = (JSONArray) args[0];
                Log.e("EMERGENCIES: ", data.toString());
                emergencyList.clear();
                for (int i = 0; i < data.length(); i++) {
                    try {
                        if(!data.getJSONObject(i).getJSONObject("reporter").get("accId").equals(db.GetID())){
                            emergencyList.add(new EmergencyObject(data.getJSONObject(i).getString("code"), data.getJSONObject(i).getString("time"), data.getJSONObject(i).getString("description"),
                                    new LatLng(data.getJSONObject(i).getJSONObject("coords").getDouble("lat"), data.getJSONObject(i).getJSONObject("coords").getDouble("lng")),
                                    data.getJSONObject(i).getJSONObject("reporter").getString("fullName"),  data.getJSONObject(i).getJSONObject("reporter").getString("pnum")));
                            Log.e("PHONE NUMBER", data.getJSONObject(i).getJSONObject("reporter").getString("pnum"));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

        mSocket.on("hospital list update", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.e("Update", "Hospitals updated");
                JSONArray data = (JSONArray) args[0];
                Log.e("EMERGENCIES: ", data.toString());
                hospitalsList.clear();
                for (int i = 0; i < data.length(); i++) {
                    try {
                        hospitalsList.add(new HospitalObject(new LatLng(data.getJSONObject(i).getDouble("lat"), data.getJSONObject(i).getDouble("lng"))));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

        mSocket.on("ambulance sent", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.e("HOSPITAL RESPONSE", "Ambulance sent");
                JSONObject data = (JSONObject) args[0];
                Log.e("EMERGENCIES: ", data.toString());
                try {
                    if(data.getString("reporterId").equals(db.GetID())) {
                        makeNotification();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        tt = new TimerTask() {
            @Override
            public void run() {
                if(!added){
                    AddUser();
                    added = true;
                }
                if(LocationUpdateService.mLastLocation != null && mSocket.connected()){
                    JSONObject locationUpdate = new JSONObject();
                    try {
                        locationUpdate.put("accId", db.GetID());
                        locationUpdate.put("accToken", db.GetToken());
                        locationUpdate.put("accRole", db.GetRole());
                        locationUpdate.put("lat", LocationUpdateService.mLastLocation.getLatitude());
                        locationUpdate.put("lng", LocationUpdateService.mLastLocation.getLongitude());
                        Log.e("LOCATION BEING SENT", String.valueOf(LocationUpdateService.mLastLocation));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mSocket.emit("user location update", locationUpdate);
                }
            }

        };

        try {
            Timer t = new Timer();
            t.scheduleAtFixedRate(tt, 0, 5 * 1000);
        } catch (Exception e){

        }


        return START_STICKY;
    }

    public String idToType(String id) {

        String output;

        switch (id) {
            case "1":
                output = "Accident";
                break;
            case "2":
                output = "Fire";
                break;
            case "3":
                output = "Terrorist Attack";
                break;
            case "4":
                output = "Shooting";
                break;
            case "5":
                output = "Not Specified";
                break;
            case "50":
                output = "Safe Drive Report";
                break;
            default:
                output = "Not Specified";
                break;
        }

        return output;

    }

    public void AddUser(){

        db = new DBHandler(WebSocketService.this, null, null, 1);

        final JSONObject userInfo = new JSONObject();

        if(LocationUpdateService.mLastLocation != null){
            try {
                userInfo.put("accId", db.GetID());
                userInfo.put("accToken", db.GetToken());
                userInfo.put("accRole", db.GetRole());
                userInfo.put("lat", LocationUpdateService.mLastLocation.getLatitude());
                userInfo.put("lng", LocationUpdateService.mLastLocation.getLongitude());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.e("LOCATION THING", "USER ADDED");
            mSocket.emit("add user", userInfo);
        } else {
            Timer tt = new Timer();
            tt.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        userInfo.put("accId", db.GetID());
                        userInfo.put("accToken", db.GetToken());
                        userInfo.put("accRole", db.GetRole());
                        userInfo.put("lat", LocationUpdateService.mLastLocation.getLatitude());
                        userInfo.put("lng", LocationUpdateService.mLastLocation.getLongitude());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Log.e("LOCATION THING", String.valueOf(LocationUpdateService.mLastLocation));
                    Log.e("LOCATION THING", "USER ADDED");
                }
            }, 5000);
        }
    }

    public void makeNotification(String id, String type){

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "emergency_channel_1";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.GRAY);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }


        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);

        if(db.GetIsCertified().equals("true")) {
            notificationBuilder.setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setSmallIcon(R.drawable.notification_icon)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setContentTitle("Emergency Nearby [" + type + "]")
                    .setContentText("Please provide assistance")
                    .setSound(alarmSound)
                    .setColor(Color.RED)
                    .setVibrate(new long[]{2000, 1000});
        } else {
            notificationBuilder.setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setSmallIcon(R.drawable.notification_icon)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setContentTitle("Emergency Nearby [" + type + "]")
                    .setContentText("Please provide feedback")
                    .setSound(alarmSound)
                    .setColor(Color.RED)
                    .setVibrate(new long[]{2000, 1000});
        }



        Intent intent = new Intent(this, NearbyEmergenciesActivity.class);
        intent.putExtra("id", id);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        notificationBuilder.setContentIntent(pendingIntent);

        notificationManager.notify(Integer.parseInt(id), notificationBuilder.build());

        Log.e("ID", String.valueOf(Integer.parseInt(id)));
    }

    public void makeNotification(){

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "emergency_channel_1";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.GRAY);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }


        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);

        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.drawable.notification_icon)
                .setPriority(Notification.PRIORITY_MAX)
                .setContentTitle("Authorities Responded")
                .setContentText("Help is on its way")
                .setSound(alarmSound)
                .setColor(Color.RED)
                .setVibrate(new long[]{2000, 1000});

        notificationManager.notify(5, notificationBuilder.build());

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        JSONObject userInfo = new JSONObject();

        try {
            userInfo.put("token", db.GetToken());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mSocket.disconnect();

        isActive = false;

        Log.e("HELLO", "DISCONNECTED");
    }
}
