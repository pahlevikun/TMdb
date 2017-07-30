package com.udacity.themoviestage1.pojo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by farhan on 6/30/17.
 */

public class Trailer implements Parcelable {

    public String id;
    public String key;
    public String judul;

    public Trailer(){

    }

    public Trailer(String id, String key, String judul){
        this.id = id;
        this.key = key;
        this.judul = judul;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.key);
        dest.writeString(this.judul);
    }

    protected Trailer(Parcel in) {
        this.id = in.readString();
        this.key = in.readString();
        this.judul = in.readString();
    }

    public static final Creator<Trailer> CREATOR = new Creator<Trailer>() {
        @Override
        public Trailer createFromParcel(Parcel source) {
            return new Trailer(source);
        }

        @Override
        public Trailer[] newArray(int size) {
            return new Trailer[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public static Creator<Trailer> getCREATOR() {
        return CREATOR;
    }
}
