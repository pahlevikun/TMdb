package com.udacity.themoviestage1.pojo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by farhan on 6/30/17.
 */

public class Content implements Parcelable {

    public String id;
    public String key;
    public String movie;

    public Content(){

    }

    public Content(String id, String key, String movie){
        this.id = id;
        this.key = key;
        this.movie = movie;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.key);
        dest.writeString(this.movie);
    }

    protected Content(Parcel in) {
        this.id = in.readString();
        this.key = in.readString();
        this.movie = in.readString();
    }

    public static final Creator<Content> CREATOR = new Creator<Content>() {
        @Override
        public Content createFromParcel(Parcel source) {
            return new Content(source);
        }

        @Override
        public Content[] newArray(int size) {
            return new Content[size];
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

    public String getMovie() {
        return movie;
    }

    public void setMovie(String movie) {
        this.movie = movie;
    }

    public static Creator<Content> getCREATOR() {
        return CREATOR;
    }
}
