package com.udacity.themoviestage1.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.udacity.themoviestage1.R;
import com.udacity.themoviestage1.pojo.Review;
import com.udacity.themoviestage1.pojo.Trailer;

import java.util.ArrayList;

/**
 * Created by farhan on 6/30/17.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.ViewHolder> {
    private ArrayList<Trailer> movieData;
    private Context context;
    private Review movie;

    public TrailerAdapter(Context context, ArrayList<Trailer> movieData) {
        this.movieData = movieData;
        this.context = context;
    }

    @Override
    public TrailerAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_trailer, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerAdapter.ViewHolder viewHolder, final int i) {

        viewHolder.textView1.setText(movieData.get(i).getJudul());
        viewHolder.textView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://www.youtube.com/watch?v="+movieData.get(i).getKey();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return movieData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView textView1;

        public ViewHolder(View view) {
            super(view);
            textView1 = (TextView) view.findViewById(R.id.textViewReview1);
        }
    }

}

