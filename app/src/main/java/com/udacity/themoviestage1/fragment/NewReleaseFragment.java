package com.udacity.themoviestage1.fragment;

/**
 * Created by farhan on 6/30/17.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.udacity.themoviestage1.R;
import com.udacity.themoviestage1.adapter.GenreAdapter;
import com.udacity.themoviestage1.adapter.MovieAdapter;
import com.udacity.themoviestage1.adapter.RecommendAdapter;
import com.udacity.themoviestage1.config.APIConfig;
import com.udacity.themoviestage1.pojo.Genre;
import com.udacity.themoviestage1.pojo.Movie;
import com.udacity.themoviestage1.pojo.Recommend;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class NewReleaseFragment extends Fragment {

    private ProgressDialog loading;
    private RecyclerView recyclerView;
    private ArrayList<Movie> valueList = new ArrayList<>();
    private MovieAdapter adapter;
    private LinearLayoutManager layoutManager;


    private RecyclerView recyclerView2;
    private ArrayList<Genre> valueList2 = new ArrayList<>();
    private ArrayList<Genre> genreList = new ArrayList<>();
    private GenreAdapter adapter2;
    private LinearLayoutManager layoutManager2;

    public NewReleaseFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie, container, false);

        String url = APIConfig.MAIN_NEW + getString(R.string.API_KEY);
        getNew(url);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(getActivity(), calculateNoOfColumns(getActivity()));
        recyclerView.setLayoutManager(layoutManager);

        adapter = new MovieAdapter(getActivity(), valueList);
        recyclerView.setAdapter(adapter);

        recyclerView2 = (RecyclerView) view.findViewById(R.id.recyclerView2);
        recyclerView2.setHasFixedSize(true);
        layoutManager2 = new GridLayoutManager(getActivity(), calculateNoOfColumns(getActivity()));
        recyclerView2.setLayoutManager(layoutManager2);

        adapter2 = new GenreAdapter(getActivity(), genreList);
        recyclerView2.setAdapter(adapter2);


        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("Nilai", 1);

        int position = 0;
        if (layoutManager != null) {
            position = layoutManager.findFirstVisibleItemPosition();
        }
        savedInstanceState.putString("position", "" + position);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            final int position = Integer.parseInt(savedInstanceState.getString("position"));

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    recyclerView.scrollToPosition(position);
                }
            }, 200);
        }
    }

    private void getNew(String url) {
        loading = ProgressDialog.show(getActivity(), "Please wait", "Now loading...", false, false);

        RequestQueue queue = Volley.newRequestQueue(getActivity());

        StringRequest postRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    Log.d("RESPON", "" + response);

                    int genreSimpan = 0;
                    JSONArray results = jObj.getJSONArray("results");
                    for (int i = 0; i < results.length(); i++) {
                        JSONObject data = results.getJSONObject(i);
                        String idMovie = data.getString("id");
                        String title = data.getString("title");
                        String popularity = data.getString("popularity");
                        String poster_path = data.getString("poster_path");
                        String original_language = data.getString("original_language");
                        String original_title = data.getString("original_title");
                        String release_date = data.getString("release_date");
                        valueList.add(new Movie(String.valueOf(i), idMovie, title, popularity, poster_path, original_language, original_title, release_date));

                        JSONArray genre = data.getJSONArray("genre_ids");
                        if (genre.length() == 0) {
                            genreSimpan = 0;
                        } else {
                            genreSimpan = genre.getInt(0);
                        }
                        Log.d("HASIL", " genre " + genreSimpan + " " + genre);
                        valueList2.add(new Genre(String.valueOf(i), idMovie, title, popularity, poster_path, original_language, original_title, release_date, genreSimpan));
                    }

                    int[] genreArray = new int[valueList2.size()];
                    for (int j = 0; j < valueList2.size(); j++) {
                        genreArray[j] = valueList2.get(j).getGenre();
                    }

                    int batasan = getPopularElement(genreArray);

                    for (int k = 0; k < valueList2.size(); k++) {
                        //for (int k=0;k<3;k++){
                        Log.d("HASIL", "ke-" + k + " " + valueList2.get(k).getGenre() + " batasan " + batasan);
                        if (valueList2.get(k).getGenre() == batasan) {
                            if (genreList.size() < 4) {
                                genreList.add(new Genre(String.valueOf(k),
                                        valueList2.get(k).getIdMovie(),
                                        valueList2.get(k).getTitle(),
                                        valueList2.get(k).getPopularity(),
                                        valueList2.get(k).getPoster_path(),
                                        valueList2.get(k).getOriginal_language(),
                                        valueList2.get(k).getOriginal_title(),
                                        valueList2.get(k).getRealease_date(),
                                        valueList2.get(k).getGenre()));
                            }
                        }
                    }

                    Log.d("TAG", "HASIL " + genreList.size() + " " + valueList.size());
                } catch (JSONException e) {
                    hideDialog();

                    Toast.makeText(getActivity(), "JSON Error : " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
                adapter.notifyDataSetChanged();
                adapter2.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Can't connect to Server", Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                return headers;
            }
        };

        int socketTimeout = 20000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        queue.add(postRequest);
    }

    public int getPopularElement(int[] a) {
        int count = 1, tempCount;
        int popular = a[0];
        int temp = 0;
        for (int i = 0; i < (a.length - 1); i++) {
            temp = a[i];
            tempCount = 0;
            for (int j = 1; j < a.length; j++) {
                if (temp == a[j])
                    tempCount++;
            }
            if (tempCount > count) {
                popular = temp;
                count = tempCount;
            }
        }
        return popular;
    }

//    private void getRecommend() {
//
//        RequestQueue queue = Volley.newRequestQueue(getActivity());
//
//        StringRequest postRequest = new StringRequest(Request.Method.GET, APIConfig.API_GET, new Response.Listener<String>() {
//
//            @Override
//            public void onResponse(String response) {
//                hideDialog();
//
//                try {
//                    JSONObject jObj = new JSONObject(response);
//                    Log.d("RESPON",""+response);
//
//                    JSONObject interest = jObj.getJSONObject("interest");
//                    JSONArray data = interest.getJSONArray("data");
//                    for (int i=0; i<data.length();i++){
//                        JSONObject film = data.getJSONObject(i);
//                        String id = film.getString("id");
//                        String nama = film.getString("title");
//                        String poster = film.getString("poster");
//                        valueList2.add(new Recommend(id,nama,poster));
//                    }
//
//                    JSONObject rating = jObj.getJSONObject("rating");
//                    JSONArray data2 = rating.getJSONArray("data");
//                    for (int i=0; i<data2.length();i++){
//                        JSONObject film = data2.getJSONObject(i);
//                        String id = film.getString("id");
//                        String nama = film.getString("title");
//                        String poster = film.getString("poster");
//                        valueList2.add(new Recommend(id,nama,poster));
//                    }
//
//                } catch (JSONException e) {
//                    Toast.makeText(getActivity(), "JSON Error : " + e.getMessage(), Toast.LENGTH_LONG).show();
//                }
//                adapter2.notifyDataSetChanged();
//            }
//        }, new Response.ErrorListener() {
//            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(getActivity(), "Can't connect to Server", Toast.LENGTH_LONG).show();
//                hideDialog();
//            }
//        }) {
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<String, String>();
//                return params;
//            }
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> headers = new HashMap<String, String>();
//                return headers;
//            }
//        };
//
//        int socketTimeout = 20000;
//        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
//        postRequest.setRetryPolicy(policy);
//        queue.add(postRequest);
//    }

    private void hideDialog() {
        if (loading.isShowing())
            loading.dismiss();
    }

    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int scalingFactor = 180;
        int noOfColumns = (int) (dpWidth / scalingFactor);
        return noOfColumns;
    }

}