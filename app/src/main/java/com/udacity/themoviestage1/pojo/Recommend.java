package com.udacity.themoviestage1.pojo;

/**
 * Created by farhan on 9/29/17.
 */

public class Recommend {
    private int _id;
    private String id, judul, poster;

    public Recommend() {

    }

    public Recommend(int _id, String id, String judul, String poster) {
        this._id = _id;
        this.id = id;
        this.judul = judul;
        this.poster = poster;
    }

    public Recommend(String id, String judul, String poster) {
        this.id = id;
        this.judul = judul;
        this.poster = poster;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }
}
