package com.asd.vikrant.zypp.dao;

public class RideDataList {

    String ride_name;
    String ride_start;
    String ride_end;
    String distance;
    String lat;
    String lng;



    public RideDataList(String ride_name, String ride_start, String ride_end, String lat, String lng)
    {
        this.distance = distance;
        this.lat = lat;
        this.lng = lng;
        this.ride_end = ride_end;
        this.ride_start= ride_start;
        this.ride_name = ride_name;
    }

    public RideDataList(String ride_name, String ride_start, String ride_end, String distance)
    {
        this.distance = distance;
        this.ride_end = ride_end;
        this.ride_start= ride_start;
        this.ride_name = ride_name;
    }

    public String getRide_name() {
        return ride_name;
    }

    public void setRide_name(String ride_name) {
        this.ride_name = ride_name;
    }

    public String getRide_start() {
        return ride_start;
    }

    public void setRide_start(String ride_start) {
        this.ride_start = ride_start;
    }

    public String getRide_end() {
        return ride_end;
    }

    public void setRide_end(String ride_end) {
        this.ride_end = ride_end;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

}
