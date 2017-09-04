package com.udacity.themoviestage1.pojo;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by farhan on 8/8/17.
 */

public class Markers {
    private int id;
    private String nama, lat, lng;

    public Markers(int id, String nama, String lat, String lng){
        this.id = id;
        this.nama = nama;
        this.lat = lat;
        this.lng = lng;
    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
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
