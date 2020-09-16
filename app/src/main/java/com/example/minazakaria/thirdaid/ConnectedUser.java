package com.example.minazakaria.thirdaid;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Mina Zakaria on 4/17/2018.
 */

public class ConnectedUser {

    private String id;
    private LatLng currentLocation;

    public ConnectedUser(LatLng loc, String id){
        this.id = id;
        this.currentLocation = loc;
    }

    public LatLng getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(LatLng currentLocation) {
        this.currentLocation = currentLocation;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
