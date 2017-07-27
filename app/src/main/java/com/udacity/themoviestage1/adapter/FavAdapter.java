package com.udacity.themoviestage1.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.udacity.themoviestage1.R;
import com.udacity.themoviestage1.contentprovider.Provider;
import com.udacity.themoviestage1.pojo.Content;
import java.util.ArrayList;

/**
 * Created by farhan on 6/30/17.
 */

public class FavAdapter extends RecyclerView.Adapter<FavAdapter.ViewHolder> {
    private ArrayList<Content> movieData;
    private Context context;
    private Content movie;

    public FavAdapter(Context context, ArrayList<Content> movieData) {
        this.movieData = movieData;
        this.context = context;
    }

    @Override
    public FavAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_fav, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FavAdapter.ViewHolder viewHolder, final int i) {

        viewHolder.textView1.setText(movieData.get(i).getMovie());
        viewHolder.textView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.getContentResolver().delete(Provider.CONTENT_URI,Provider._ID + "=" + movieData.get(i).getId(), null);
                ((Activity)context).finish();
                Toast.makeText(context, "Success delete favorite!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return movieData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView textView1,textView2;

        public ViewHolder(View view) {
            super(view);
            textView1 = (TextView) view.findViewById(R.id.textViewFav1);
        }
    }

}

