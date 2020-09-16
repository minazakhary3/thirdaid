package com.example.minazakaria.thirdaid;

import android.location.Location;
import android.location.LocationManager;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Mina Zakaria on 4/16/2018.
 */

public class EmergencyObject {
    String type;
    String time;
    String description;
    String fullName;
    float distance;
    String pnum;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPnum() {
        return pnum;
    }

    public void setPnum(String pnum) {
        this.pnum = pnum;
    }

    LatLng location;

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public EmergencyObject(String type1, String time, String description, LatLng location, String fullName, String pnum) {
        switch (type1){
            case "1": this.type = "Accident"; break;
            case "2": this.type = "Fire"; break;
            case "3": this.type = "Terrorist Attack"; break;
            case "4": this.type = "Shooting"; break;
            case "50": this.type = "Safe Drive Report"; break;
            case "5": this.type = "Not Specified"; break;
            default: this.type = "Not Specified"; break;
            }
        Location temp = new Location(LocationManager.GPS_PROVIDER);
        temp.setLatitude(location.latitude);
        temp.setLongitude(location.longitude);
        this.time = time;
        this.description = description;
        this.distance = LocationUpdateService.mLastLocation.distanceTo(temp) / 1000;
        this.location = location;
        this.fullName = fullName;
        this.pnum = pnum;
    }

    public String getType() {
        return type;
    }

    public void setType(String title) {
        this.type = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }
}
