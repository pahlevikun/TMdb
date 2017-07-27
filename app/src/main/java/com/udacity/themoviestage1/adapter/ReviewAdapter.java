package com.udacity.themoviestage1.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.udacity.themoviestage1.R;
import com.udacity.themoviestage1.pojo.Review;

import java.util.ArrayList;

/**
 * Created by farhan on 6/30/17.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {
    private ArrayList<Review> movieData;
    private Context context;
    private Review movie;

    public ReviewAdapter(Context context, ArrayList<Review> movieData) {
        this.movieData = movieData;
        this.context = context;
    }

    @Override
    public ReviewAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_review, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewAdapter.ViewHolder viewHolder, final int i) {

        viewHolder.textView1.setText(movieData.get(i).getAuthor());
        viewHolder.textView2.setText(movieData.get(i).getContents());
    }

    @Override
    public int getItemCount() {
        return movieData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView textView1,textView2;

        public ViewHolder(View view) {
            super(view);
            textView1 = (TextView) view.findViewById(R.id.textViewReview1);
            textView2 = (TextView) view.findViewById(R.id.textViewReview2);
        }
    }

}

