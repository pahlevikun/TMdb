package com.udacity.themoviestage1.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.udacity.themoviestage1.DetailActivity;
import com.udacity.themoviestage1.R;
import com.udacity.themoviestage1.config.APIConfig;
import com.udacity.themoviestage1.pojo.Movie;
import com.udacity.themoviestage1.pojo.Recommend;

import java.util.ArrayList;

/**
 * Created by farhan on 6/30/17.
 */

public class RecommendAdapter extends RecyclerView.Adapter<RecommendAdapter.ViewHolder> {
    private ArrayList<Recommend> movieData;
    private Context context;
    private Movie movie;

    public RecommendAdapter(Context context, ArrayList<Recommend> movieData) {
        this.movieData = movieData;
        this.context = context;
    }

    @Override
    public RecommendAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_movie, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecommendAdapter.ViewHolder viewHolder, final int i) {

        viewHolder.textView1.setText(movieData.get(i).getJudul());
        viewHolder.textView2.setVisibility(View.GONE);
        Picasso.with(context)
                .load(APIConfig.BASE_IMAGE+movieData.get(i).getPoster())
                .error(R.drawable.ic_image)
                .into(viewHolder.imageView);
    }

    @Override
    public int getItemCount() {
        return movieData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView textView1,textView2;
        private ImageView imageView;
        private CardView cardView;

        public ViewHolder(View view) {
            super(view);
            textView1 = (TextView) view.findViewById(R.id.textViewMovie1);
            textView2 = (TextView) view.findViewById(R.id.textViewMovie2);
            imageView = (ImageView) view.findViewById(R.id.imageViewMovie);
            cardView = (CardView) view.findViewById(R.id.cardViewMovie);

        }
    }

}

