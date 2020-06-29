package com.example.dani_pc.bobo;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import java.util.ArrayList;

public class TopScoreFragment extends Fragment {

    private ArrayList<TopScore> recordScores;
    private String dataTable;
    private DataBase db;


    public TopScoreFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = DataBase.getInstance(getActivity().getApplicationContext());

        Bundle bundle = this.getArguments();
        dataTable = bundle.getString(TopScoreActivity.TABLE_NAME_KEY);

        synchronized (db) {
            recordScores = db.getAllRecordsFromTable(dataTable);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_top_scorelist, container, false);
        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;

            recyclerView.setLayoutManager(new LinearLayoutManager(context));


            recyclerView.setAdapter(new TopScoreAdapter(recordScores, new TopScoreAdapter.OnItemClickListener() {

                @Override
                public void onItemClick(TopScore item) {

                    TopScore hs = new TopScore(item.getId(),item.getName(),item.getScore(),item.getLatitude(),item.getLongtitude());
                    ((TopScoreActivity)getActivity()).getRecordFromClickedList(hs);

                }
            }));
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}

