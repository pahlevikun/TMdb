package com.udacity.themoviestage1;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.udacity.themoviestage1.adapter.FavAdapter;
import com.udacity.themoviestage1.adapter.ReviewAdapter;
import com.udacity.themoviestage1.contentprovider.Provider;
import com.udacity.themoviestage1.pojo.Content;
import com.udacity.themoviestage1.pojo.Movie;
import com.udacity.themoviestage1.pojo.Review;

import java.util.ArrayList;

public class FavActivity extends AppCompatActivity {

    public ArrayList<Content> valueList = new ArrayList<>();
    public RecyclerView recyclerView;
    public FavAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        setTitle("Your Favorite Movies");

        getContent();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(FavActivity.this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new FavAdapter(FavActivity.this,valueList);
        recyclerView.setAdapter(adapter);

    }

    private void getContent() {
        String URL = "content://com.udacity.themoviestage1.contentprovider.Provider";

        Uri movie = Uri.parse(URL);
        Cursor c = managedQuery(movie, null, null, null, Provider._ID+" ASC");
        if (c.moveToFirst()) {
            do{
                valueList.add(new Content(c.getString(c.getColumnIndex(Provider._ID)),
                        c.getString(c.getColumnIndex(Provider.MOVIE)),
                        c.getString(c.getColumnIndex(Provider.TITLE))));
            } while (c.moveToNext());
        }
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
