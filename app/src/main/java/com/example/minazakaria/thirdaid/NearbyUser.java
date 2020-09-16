package com.example.minazakaria.thirdaid;

/**
 * Created by Mina Zakaria on 4/14/2018.
 */

public class NearbyUser {

    private double lat;
    private double lng;
    private double distance;
    private String fullName;

    public NearbyUser(double lat, double lng, double distance, String fullName){
        this.lat = lat;
        this.lng = lng;
        this.distance = distance;
        this.fullName = fullName;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
