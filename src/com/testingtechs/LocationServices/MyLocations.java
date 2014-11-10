package com.testingtechs.LocationServices;

import android.location.Location;

/**
 * Created by matthewmcguire on 11/9/14.
 */
public class MyLocations {
    private double latitude, longitude, time;
    private String ID = null;

    public MyLocations(){}

    public MyLocations(String id) {
        this.ID = id;
    }

    protected void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    protected void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    protected void setTime(double time) {
        this.time = time;
    }

    protected double getLatitude() {
        return this.latitude;
    }

    protected  double getLongitude() {
        return this.longitude;
    }

    protected double getTime() {
        return this.time;
    }

    protected String getId() {
        return this.ID;
    }

    protected void reset() {
        this.latitude = 0.0;
        this.longitude = 0.0;
        this.time = 0.0;
    }
}
