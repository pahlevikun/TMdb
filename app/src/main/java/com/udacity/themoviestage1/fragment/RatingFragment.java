package com.udacity.themoviestage1.fragment;

/**
 * Created by farhan on 6/30/17.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
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
import com.udacity.themoviestage1.adapter.MovieAdapter;
import com.udacity.themoviestage1.config.APIConfig;
import com.udacity.themoviestage1.pojo.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class RatingFragment extends Fragment{

    private ProgressDialog loading;
    private RecyclerView recyclerView;
    private ArrayList<Movie> valueList = new ArrayList<>();
    private MovieAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    public RatingFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie, container, false);

        String url = APIConfig.MAIN_RATING +getString(R.string.API_KEY);
        getRating(url);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(getActivity(),calculateNoOfColumns(getActivity()));
        recyclerView.setLayoutManager(layoutManager);

        adapter = new MovieAdapter(getActivity(),valueList);
        recyclerView.setAdapter(adapter);


        return view;
    }

    private void getRating(String url) {
        loading = ProgressDialog.show(getActivity(),"Please wait","Now loading...",false,false);

        RequestQueue queue = Volley.newRequestQueue(getActivity());

        StringRequest postRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    Log.d("RESPON",""+response);

                    JSONArray results = jObj.getJSONArray("results");
                    for (int i = 0; i < results.length(); i++){
                        JSONObject data = results.getJSONObject(i);
                        String idMovie = data.getString("id");
                        String title = data.getString("title");
                        String popularity = data.getString("popularity");
                        String poster_path = data.getString("poster_path");
                        String original_language = data.getString("original_language");
                        String original_title = data.getString("original_title");
                        String release_date = data.getString("release_date");
                        valueList.add(new Movie(String.valueOf(i),idMovie,title,popularity,poster_path,original_language,original_title,release_date));
                    }
                    Log.d("HASIL"," "+valueList.size());
                } catch (JSONException e) {
                    Toast.makeText(getActivity(), "Json Error : " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
                adapter.notifyDataSetChanged();
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