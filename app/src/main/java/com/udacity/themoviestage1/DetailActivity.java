package com.udacity.themoviestage1;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.squareup.picasso.Picasso;
import com.udacity.themoviestage1.adapter.MovieAdapter;
import com.udacity.themoviestage1.adapter.RecommendAdapter;
import com.udacity.themoviestage1.config.APIConfig;
import com.udacity.themoviestage1.contentprovider.Provider;
import com.udacity.themoviestage1.pojo.Movie;
import com.udacity.themoviestage1.pojo.Recommend;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DetailActivity extends AppCompatActivity  {

    private String title, shareBody,url,backdrop_path,idMovie;

    private FloatingActionButton floatingActionButton;
    private TextView tvTitle, tvVote, tvDate, tvPlot;
    private ImageView ivBackground, ivDetail;
    private Button btVideo;

    private ProgressDialog loading;

    private Movie movie;


    private RecyclerView recyclerView;
    private ArrayList<Recommend> valueList = new ArrayList<>();
    private RecommendAdapter adapter;
    private LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingActionButtonDetail);
        tvTitle = (TextView) findViewById(R.id.textViewTitleDetail);
        tvVote = (TextView) findViewById(R.id.textViewVoteDetail);
        tvDate = (TextView) findViewById(R.id.textViewDateDetail);
        tvPlot = (TextView) findViewById(R.id.textViewPlotDetail);
        ivBackground = (ImageView) findViewById(R.id.imageViewBackDetail);
        ivDetail = (ImageView) findViewById(R.id.imageViewInfoDetail);
        btVideo = (Button) findViewById(R.id.buttonVideo);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appBarLayoutDetail);

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            appBarLayout.setElevation(0);
        }
        appBarLayout.bringToFront();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(DetailActivity.this,2);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new RecommendAdapter(DetailActivity.this,valueList);
        recyclerView.setAdapter(adapter);

        Intent intent = getIntent();
        if(intent != null){
            if(intent.hasExtra("parcel")){
                movie = intent.getParcelableExtra("parcel");
                idMovie = movie.idMovie;
                Log.d("Hasil"," "+movie.idMovie);
                title = movie.title;
                url = APIConfig.DETAIL + idMovie + "?api_key=" + getString(R.string.API_KEY);
            }else if (intent.hasExtra("kunci")){
                title = intent.getStringExtra("judul");
                url = APIConfig.DETAIL + intent.getStringExtra("kunci") + "?api_key=" + getString(R.string.API_KEY);
            }else if (intent.hasExtra("recommend")){
                title = intent.getStringExtra("judul");
                idMovie = intent.getStringExtra("idMovie");
                url = APIConfig.DETAIL + idMovie + "?api_key=" + getString(R.string.API_KEY);
            }else{
                finish();
            }
        }

        getDetail(url);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Share "+title+" Information");
                intent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(intent,"Share via"));
            }
        });

        btVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getKey(movie.idMovie);
            }
        });
    }


    private void getDetail(String url) {
        loading = ProgressDialog.show(DetailActivity.this,"Please wait","Now loading...",false,false);

        RequestQueue queue = Volley.newRequestQueue(DetailActivity.this);

        StringRequest postRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jObj = new JSONObject(response);
                    Log.d("RESPON",""+response);
                    String release_date = jObj.getString("release_date");
                    String voting = jObj.getString("vote_average");
                    String overview = jObj.getString("overview");
                    String poster_path = jObj.getString("poster_path");
                    backdrop_path = jObj.getString("backdrop_path");

                    if(backdrop_path.isEmpty()||backdrop_path.equals(null)||backdrop_path==null||backdrop_path.equals("null")){
                        ivBackground.setBackgroundResource(R.color.colorPrimary);
                    }else{
                        Picasso.with(DetailActivity.this).load(APIConfig.BASE_BACKDROP+backdrop_path).into(ivBackground);
                    }
                    Picasso.with(DetailActivity.this).load(APIConfig.BASE_IMAGE+poster_path).into(ivDetail);
                    if(overview.length()==0){
                        tvPlot.setText("Plot not available");
                    }else {
                        tvDate.setText(release_date);
                        tvPlot.setText(overview);
                        tvVote.setText(voting);
                        tvTitle.setText(title);
                    }
                    setTitle(title);
                    shareBody = title+"\nRelease date : "+release_date+"\nPlot : "+overview;
                    addMovie(title,poster_path,voting);
                } catch (JSONException e) {hideDialog();

                    Toast.makeText(DetailActivity.this, "JSON Error : " + e.getMessage(), Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(DetailActivity.this, "Can't connect to Server", Toast.LENGTH_LONG).show();
                hideDialog();
                finish();
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

    private void getKey(final String id) {
        loading = ProgressDialog.show(DetailActivity.this,"Please wait","Now loading...",false,false);

        RequestQueue queue = Volley.newRequestQueue(DetailActivity.this);
        String url = APIConfig.DETAIL + id + "/videos?api_key=" + getString(R.string.API_KEY);

        StringRequest postRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    JSONArray results = jObj.getJSONArray("results");
                    JSONObject i = results.getJSONObject(0);
                    String key = i.getString("key");

                    Intent intent =  new Intent(DetailActivity.this, VideoActivity.class);
                    intent.putExtra("key",key);
                    intent.putExtra("id",id);
                    startActivity(intent);
                } catch (JSONException e) {
                    Toast.makeText(DetailActivity.this, "JSON Error : " + e.getMessage(), Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(DetailActivity.this, "Can't connect to Server", Toast.LENGTH_LONG).show();
                hideDialog();
                finish();
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

    private void addMovie(final String title, final String poster, final String rating) {
        RequestQueue queue = Volley.newRequestQueue(DetailActivity.this);

        StringRequest postRequest = new StringRequest(Request.Method.POST, APIConfig.API_ADD, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    getRecommend();
                } catch (JSONException e) {
                    Toast.makeText(DetailActivity.this, "JSON Error : " + e.getMessage(), Toast.LENGTH_LONG).show();
                    hideDialog();
                    finish();
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(DetailActivity.this, "Can't connect to Server", Toast.LENGTH_LONG).show();
                hideDialog();
                finish();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("id",idMovie);
                params.put("title",title);
                params.put("poster",poster);
                params.put("rating",rating);
                params.put("interest","1");
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

    private void getRecommend() {

        RequestQueue queue = Volley.newRequestQueue(DetailActivity.this);

        StringRequest postRequest = new StringRequest(Request.Method.GET, APIConfig.API_GET, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    Log.d("RESPON",""+response);

                    JSONObject interest = jObj.getJSONObject("interest");
                    JSONArray data = interest.getJSONArray("data");
                    for (int i=0; i<data.length();i++){
                        JSONObject film = data.getJSONObject(i);
                        String id = film.getString("id");
                        String nama = film.getString("title");
                        String poster = film.getString("poster");
                        valueList.add(new Recommend(id,nama,poster));
                    }

                    JSONObject rating = jObj.getJSONObject("rating");
                    JSONArray data2 = rating.getJSONArray("data");
                    for (int i=0; i<data2.length();i++){
                        JSONObject film = data2.getJSONObject(i);
                        String id = film.getString("id");
                        String nama = film.getString("title");
                        String poster = film.getString("poster");
                        valueList.add(new Recommend(id,nama,poster));
                    }

                } catch (JSONException e) {
                    Toast.makeText(DetailActivity.this, "JSON Error : " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
                adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(DetailActivity.this, "Can't connect to Server", Toast.LENGTH_LONG).show();
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

    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.fav, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id==android.R.id.home) {
            finish();
            return true;
        }else if(id==R.id.action_fav) {
            try{
                ContentValues values = new ContentValues();
                values.put(Provider.TITLE, title);
                values.put(Provider.MOVIE, id);
                values.put(Provider.IMAGE, backdrop_path);
                Uri uri = getContentResolver().insert(Provider.CONTENT_URI, values);

                Toast.makeText(getBaseContext(), uri.toString(), Toast.LENGTH_LONG).show();
            }catch (Exception e){
                Toast.makeText(this, "You already add it to Favorite!", Toast.LENGTH_SHORT).show();
            }
            //provider.insert(title,id,backdrop_path);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
