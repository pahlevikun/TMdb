package com.udacity.themoviestage1;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ScrollView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.udacity.themoviestage1.adapter.ReviewAdapter;
import com.udacity.themoviestage1.adapter.TrailerAdapter;
import com.udacity.themoviestage1.config.APIConfig;
import com.udacity.themoviestage1.contentprovider.Provider;
import com.udacity.themoviestage1.pojo.Review;
import com.udacity.themoviestage1.pojo.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class VideoActivity extends YouTubeBaseActivity implements
        YouTubePlayer.OnInitializedListener {

    private static final int RECOVERY_DIALOG_REQUEST = 1;
    private String idMovie,id;

    private ProgressDialog loading;
    private RecyclerView recyclerView, recyclerView2;
    private ArrayList<Review> valueList = new ArrayList<>();
    private ArrayList<Trailer> trailerList = new ArrayList<>();
    private ReviewAdapter adapter;
    private TrailerAdapter trailerAdapter;

    private YouTubePlayerView youTubeView;

    private ScrollView scrollView;

    private Bundle savedInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        Intent intent = getIntent();
        idMovie = intent.getStringExtra("key");
        id = intent.getStringExtra("id");

        youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
        scrollView = (ScrollView) findViewById(R.id.scrollView);

        String url = APIConfig.DETAIL + id + "/reviews?api_key=" + getString(R.string.API_KEY);
        getNew(url);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(VideoActivity.this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new ReviewAdapter(VideoActivity.this,valueList);
        recyclerView.setAdapter(adapter);

        recyclerView2 = (RecyclerView) findViewById(R.id.recyclerView2);
        RecyclerView.LayoutManager mLayoutManager2 = new LinearLayoutManager(VideoActivity.this);
        recyclerView2.setLayoutManager(mLayoutManager2);
        recyclerView2.setItemAnimator(new DefaultItemAnimator());
        trailerAdapter = new TrailerAdapter(VideoActivity.this,trailerList);
        recyclerView2.setAdapter(trailerAdapter);

        recyclerView.setNestedScrollingEnabled(false);
        recyclerView2.setNestedScrollingEnabled(false);
    }

    //  two static variable,
    public static int scrollX = 0;
    public static int scrollY = -1;

    @Override
    protected void onPause() {
        super.onPause();
        scrollX = scrollView.getScrollX();
        scrollY = scrollView.getScrollY();
    }
    @Override
    protected void onResume() {
        super.onResume();
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.scrollTo(scrollX, scrollY);
            }
        });
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntArray("ARTICLE_SCROLL_POSITION", new int[]{ scrollView.getScrollX(), scrollView.getScrollY()});
        Log.d("SCROLL", ""+scrollView.getScrollX()+scrollView.getScrollY());
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        final int[] position = savedInstanceState.getIntArray("ARTICLE_SCROLL_POSITION");
        if(position != null)
            scrollView.post(new Runnable() {
                public void run() {scrollView.scrollTo(position[0], position[1]);
                }
            });
        Log.d("SCROLL",""+position[0]+" "+position[1]);
    }

    private void getNew(String url) {
        loading = ProgressDialog.show(VideoActivity.this,"Please wait","Now loading...",false,false);

        RequestQueue queue = Volley.newRequestQueue(VideoActivity.this);

        StringRequest postRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jObj = new JSONObject(response);
                    Log.d("RESPON",""+response);

                    JSONArray results = jObj.getJSONArray("results");
                    for (int i = 0; i < results.length(); i++){
                        JSONObject data = results.getJSONObject(i);
                        String author = data.getString("author");
                        String contens = data.getString("content");
                        String urls = data.getString("url");
                        valueList.add(new Review(String.valueOf(i),author,contens,urls));
                    }
                    youTubeView.initialize(getString(R.string.YOUTUBE_KEY), VideoActivity.this);

                    String url = APIConfig.DETAIL + id + "?append_to_response=videos&api_key=" + getString(R.string.API_KEY);
                    getTrailer(url);
                } catch (JSONException e) {
                    hideDialog();
                    Toast.makeText(VideoActivity.this, "JSON Error : " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
                adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(VideoActivity.this, "Can't connect to Server", Toast.LENGTH_LONG).show();
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

    private void getTrailer(String url) {

        RequestQueue queue = Volley.newRequestQueue(VideoActivity.this);

        StringRequest postRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    Log.d("RESPON",""+response);
                    JSONObject video = jObj.getJSONObject("videos");

                    JSONArray results = video.getJSONArray("results");
                    for (int i = 0; i < results.length(); i++){
                        JSONObject data = results.getJSONObject(i);
                        String key = data.getString("key");
                        String name = data.getString("name");
                        trailerList.add(new Trailer(String.valueOf(i),key,name));
                    }
                    Log.d("RESPON"," "+trailerList.size());
                } catch (JSONException e) {
                    Toast.makeText(VideoActivity.this, "JSON Error : " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
                adapter.notifyDataSetChanged();
                trailerAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(VideoActivity.this, "Can't connect to Server", Toast.LENGTH_LONG).show();
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

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show();
        } else {
            String errorMessage = String.format(getString(R.string.error_player), errorReason.toString());
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
        if (!wasRestored) {
            player.loadVideo(idMovie);
            player.setPlayerStyle(YouTubePlayer.PlayerStyle.CHROMELESS);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_DIALOG_REQUEST) {
            getYouTubePlayerProvider().initialize(getString(R.string.YOUTUBE_KEY), this);
        }
    }

    private YouTubePlayer.Provider getYouTubePlayerProvider() {
        return (YouTubePlayerView) findViewById(R.id.youtube_view);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id==android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
