package com.example.dani_pc.bobo;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.ArrayList;


public class TopScoreAdapter extends RecyclerView.Adapter<TopScoreAdapter.ViewHolder>{

    public interface OnItemClickListener {
        void onItemClick(TopScore item);
    }


    private  ArrayList<TopScore> recordScores;
    private final OnItemClickListener listener;


    public TopScoreAdapter(ArrayList<TopScore> recordList, OnItemClickListener listener) {
        recordScores = recordList;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_top_score, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.item = recordScores.get(position);
        holder.name.setText(recordScores.get(position).getName());
        holder.score.setText(String.valueOf(recordScores.get(position).getScore()));

        holder.bind(recordScores.get(position),listener);

    }


    @Override
    public int getItemCount() {
        if (recordScores ==null)
            return 0;
        return recordScores.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder  {
        public final View mView;
        public final TextView name;
        public final TextView score;
        public TopScore item;


        public ViewHolder(View view ) {
            super(view);
            mView = view;
            name = (TextView) view.findViewById(R.id.highscore_name_id);
            score = (TextView) view.findViewById(R.id.highscore_score_id);
        }

        public void bind(final TopScore item , final OnItemClickListener listener){

            itemView.setOnClickListener(new View.OnClickListener() {

                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }

    }


}
