package com.udacity.themoviestage1.config;

/**
 * Created by farhan on 6/30/17.
 */

public class APIConfig {
    public static String MAIN_NEW = "https://api.themoviedb.org/3/discover/movie?with_genres=28&language=en&page=1&vote_count.gte=5&sort_by=release_date.desc&api_key=";
    public static String MAIN_POPULAR = "https://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=";
    public static String MAIN_RATING = "https://api.themoviedb.org/3/discover/movie?sort_by=vote_average.desc&page=1&vote_count.gte=50&api_key=";
    public static String DETAIL = "https://api.themoviedb.org/3/movie/";
    public static String SEARCH = "https://api.themoviedb.org/3/search/movie?api_key=";

    public static String BASE_IMAGE = "https://image.tmdb.org/t/p/w500/";
    public static String BASE_BACKDROP = "https://image.tmdb.org/t/p/w780/";
}
