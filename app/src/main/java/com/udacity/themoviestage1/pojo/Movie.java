package com.udacity.themoviestage1.pojo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by farhan on 6/30/17.
 */

public class Movie implements Parcelable {

    public String id, idMovie, title, popularity, poster_path, original_language, original_title, realease_date;

    public Movie(){

    }

    public Movie(String id, String idMovie, String title, String popularity, String poster_path, String original_language,
                    String original_title, String realease_date){
        this.id = id;
        this.idMovie = idMovie;
        this.title = title;
        this.popularity = popularity;
        this.poster_path = poster_path;
        this.original_language = original_language;
        this.original_title = original_title;
        this.realease_date = realease_date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.idMovie);
        dest.writeString(this.title);
        dest.writeString(this.popularity);
        dest.writeString(this.poster_path);
        dest.writeString(this.original_language);
        dest.writeString(this.original_title);
        dest.writeString(this.realease_date);
    }

    protected Movie(Parcel in) {
        this.id = in.readString();
        this.idMovie = in.readString();
        this.title = in.readString();
        this.popularity = in.readString();
        this.poster_path = in.readString();
        this.original_language = in.readString();
        this.original_title = in.readString();
        this.realease_date = in.readString();
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdMovie() {
        return idMovie;
    }

    public void setIdMovie(String idMovie) {
        this.idMovie = idMovie;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPopularity() {
        return popularity;
    }

    public void setPopularity(String popularity) {
        this.popularity = popularity;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public String getOriginal_language() {
        return original_language;
    }

    public void setOriginal_language(String original_language) {
        this.original_language = original_language;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public void setOriginal_title(String original_title) {
        this.original_title = original_title;
    }

    public String getRealease_date() {
        return realease_date;
    }

    public void setRealease_date(String realease_date) {
        this.realease_date = realease_date;
    }
}
