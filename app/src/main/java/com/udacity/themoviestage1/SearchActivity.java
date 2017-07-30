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
import com.udacity.themoviestage1.adapter.MovieAdapter;
import com.udacity.themoviestage1.config.APIConfig;
import com.udacity.themoviestage1.pojo.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {

    private ProgressDialog loading;
    private RecyclerView recyclerView;
    private ArrayList<Movie> valueList = new ArrayList<>();
    private MovieAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private EditText etSearch;
    private Button btSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        btSearch = (Button) findViewById(R.id.buttonSearch);
        etSearch = (EditText) findViewById(R.id.editTextSearch);

        recyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(SearchActivity.this,2);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new MovieAdapter(SearchActivity.this,valueList);
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
        loading = ProgressDialog.show(SearchActivity.this,"Please wait","Now loading...",false,false);

        RequestQueue queue = Volley.newRequestQueue(SearchActivity.this);

        StringRequest postRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                hideDialog();
                valueList.clear();
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
                    Toast.makeText(SearchActivity.this, "JSON Error : " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
                adapter.notifyDataSetChanged();
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

    private void hideDialog() {
        if (loading.isShowing())
            loading.dismiss();
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
