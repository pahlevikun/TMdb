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
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.udacity.themoviestage1.DetailActivity;
import com.udacity.themoviestage1.R;
import com.udacity.themoviestage1.config.APIConfig;
import com.udacity.themoviestage1.pojo.Movie;

import java.util.ArrayList;

/**
 * Created by farhan on 6/30/17.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {
    private ArrayList<Movie> movieData;
    private Context context;

    public MovieAdapter(Context context,ArrayList<Movie> movieData) {
        this.movieData = movieData;
        this.context = context;
    }

    @Override
    public MovieAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_movie, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieAdapter.ViewHolder viewHolder, final int i) {

        viewHolder.textView1.setText(movieData.get(i).getTitle());
        viewHolder.textView2.setText(movieData.get(i).getRealease_date());
        Picasso.with(context).load(APIConfig.BASE_IMAGE+movieData.get(i).getPoster_path()).into(viewHolder.imageView);
        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("idMovie",movieData.get(i).getIdMovie());
                intent.putExtra("title",movieData.get(i).getTitle());
                intent.putExtra("original_title",movieData.get(i).getOriginal_title());
                intent.putExtra("release_date",movieData.get(i).getRealease_date());
                context.startActivity(intent);
            }
        });
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

