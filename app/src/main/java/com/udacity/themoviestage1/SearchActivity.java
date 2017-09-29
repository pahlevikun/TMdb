package com.udacity.themoviestage1;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
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
import com.udacity.themoviestage1.adapter.GenreAdapter;
import com.udacity.themoviestage1.adapter.MovieAdapter;
import com.udacity.themoviestage1.config.APIConfig;
import com.udacity.themoviestage1.config.AprioriFrequentItemsetGenerator;
import com.udacity.themoviestage1.pojo.Genre;
import com.udacity.themoviestage1.pojo.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {

    private ProgressDialog loading;
    private RecyclerView recyclerView, recyclerSuggest;
    private ArrayList<Genre> valueList = new ArrayList<>();
    private ArrayList<Genre> genreList = new ArrayList<>();
    private GenreAdapter adapter, adapter2;
    private RecyclerView.LayoutManager layoutManager;
    private EditText etSearch;
    private Button btSearch;
    private int i = 0;
    private String[] parts;
    private TextView tvReko1, tvReko2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerSuggest = (RecyclerView) findViewById(R.id.recyclerViewSuggest);
        btSearch = (Button) findViewById(R.id.buttonSearch);
        etSearch = (EditText) findViewById(R.id.editTextSearch);
        tvReko1 = (TextView) findViewById(R.id.tvRekomen1);
        tvReko2 = (TextView) findViewById(R.id.tvRekomen2);

        recyclerSuggest.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(SearchActivity.this, 2);
        recyclerSuggest.setLayoutManager(layoutManager);

        adapter2 = new GenreAdapter(SearchActivity.this, genreList);
        recyclerSuggest.setAdapter(adapter2);

        recyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(SearchActivity.this, 2);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new GenreAdapter(SearchActivity.this, valueList);
        recyclerView.setAdapter(adapter);

        btSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String param = etSearch.getText().toString().trim();
                param = param.replace(" ","+");
                String url = APIConfig.SEARCH + getString(R.string.API_KEY) + "&query=" +param;
                Log.d("HASIL"," "+url);

                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                getSearch(url);
            }
        });

    }


    private void getSearch(String url) {
        loading = ProgressDialog.show(SearchActivity.this, "Please wait", "Now loading...", false, false);

        RequestQueue queue = Volley.newRequestQueue(SearchActivity.this);

        StringRequest postRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    Log.d("RESPON", "" + response);

                    valueList.clear();
                    genreList.clear();

                    JSONArray results = jObj.getJSONArray("results");
                    int genreSimpan;
                    for (int i = 0; i < results.length(); i++) {
                        JSONObject data = results.getJSONObject(i);
                        String idMovie = data.getString("id");
                        String title = data.getString("title");
                        String popularity = data.getString("popularity");
                        String poster_path = data.getString("poster_path");
                        String original_language = data.getString("original_language");
                        String original_title = data.getString("original_title");
                        String release_date = data.getString("release_date");
                        JSONArray genre = data.getJSONArray("genre_ids");
                        if (genre.length()==0){
                            genreSimpan = 0;
                        }else {
                            genreSimpan = genre.getInt(0);
                        }
                        Log.d("HASIL"," genre "+genreSimpan+" "+genre);
                        valueList.add(new Genre(String.valueOf(i), idMovie, title, popularity, poster_path, original_language, original_title, release_date,genreSimpan));
                    }
                    int[] genreArray = new int[valueList.size()];
                    for (int j=0;j<valueList.size();j++){
                        genreArray[j] = valueList.get(j).getGenre();
                    }

                    int batasan = getPopularElement(genreArray);
                    for (int k=0;k<valueList.size();k++){
                    //for (int k=0;k<3;k++){
                        Log.d("HASIL","ke-"+k+" "+valueList.get(i).getGenre()+" batasan "+batasan);
                        if (valueList.get(k).getGenre()==batasan){
                            if (genreList.size()<4){
                                genreList.add(new Genre(String.valueOf(k),
                                        valueList.get(k).getIdMovie(),
                                        valueList.get(k).getTitle(),
                                        valueList.get(k).getPopularity(),
                                        valueList.get(k).getPoster_path(),
                                        valueList.get(k).getOriginal_language(),
                                        valueList.get(k).getOriginal_title(),
                                        valueList.get(k).getRealease_date(),
                                        valueList.get(k).getGenre()));
                            }
                        }
                    }

                    Log.d("HASIL", " " + valueList.size()+" "+genreList.size());
                } catch (JSONException e) {
                    Toast.makeText(SearchActivity.this, "JSON Error : " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
                adapter.notifyDataSetChanged();
                adapter2.notifyDataSetChanged();
                if (valueList.size()!=0){
                    tvReko1.setVisibility(View.VISIBLE);
                    tvReko2.setVisibility(View.VISIBLE);
                }else{
                    tvReko1.setVisibility(View.GONE);
                    tvReko2.setVisibility(View.GONE);

                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SearchActivity.this, "Can't connect to Server", Toast.LENGTH_LONG).show();
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
        for (int i = 0; i < (a.length - 1); i++)
        {
            temp = a[i];
            tempCount = 0;
            for (int j = 1; j < a.length; j++)
            {
                if (temp == a[j])
                    tempCount++;
            }
            if (tempCount > count)
            {
                popular = temp;
                count = tempCount;
            }
        }
        return popular;
    }

    private void hideDialog() {
        if (loading.isShowing())
            loading.dismiss();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
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
