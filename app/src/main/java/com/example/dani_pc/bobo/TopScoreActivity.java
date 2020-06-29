package com.example.dani_pc.bobo;


import android.support.v4.app.FragmentTransaction;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.view.View;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.ArrayList;


public class TopScoreActivity extends AppCompatActivity implements OnMapReadyCallback{

    public static final String TABLE_NAME_KEY = "TABLE_NAME";
    public static final String DEFAULT_TABLE_NAME = DataBase.DB_TABLE_EASY;

    private Fragment fragment;
    private Button easyButton, mediumButton, hardButton;

    private DataBase db;
    private GoogleMap gMap;
    private ArrayList <TopScore> mAllRecords;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_score);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        db = DataBase.getInstance(this);

        Bundle bundle = new Bundle();
        bundle.putString(TABLE_NAME_KEY, DEFAULT_TABLE_NAME);

        fragment = new TopScoreFragment();
        fragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.add(R.id.high_score_layout_id, fragment);
        transaction.commit();


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



        easyButton = (Button) findViewById(R.id.easy_button_id);
        mediumButton = (Button) findViewById(R.id.medium_button_id);
        hardButton = (Button) findViewById(R.id.hard_button_id);
        mediumButton.setAlpha(.5f);
        hardButton.setAlpha(.5f);


    }

    public void easyButtonPressed(View v){

        easyButton.setAlpha(1);
        mediumButton.setAlpha(.5f);
        hardButton.setAlpha(.5f);

        Bundle bundle = new Bundle();
        bundle.putString(TABLE_NAME_KEY, DataBase.DB_TABLE_EASY);
        fragment = new TopScoreFragment();
        fragment.setArguments(bundle);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.high_score_layout_id, fragment);
        transaction.commit();


        showAllMarkersOnMap(DataBase.DB_TABLE_EASY);
    }


    public void mediumButtonPressed(View v){

        mediumButton.setAlpha(1);
        easyButton.setAlpha(.5f);
        hardButton.setAlpha(.5f);

        Bundle bundle = new Bundle();
        bundle.putString(TABLE_NAME_KEY, DataBase.DB_TABLE_MEDIUM);

        fragment = new TopScoreFragment();
        fragment.setArguments(bundle);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.high_score_layout_id, fragment);
        transaction.commit();

        showAllMarkersOnMap(DataBase.DB_TABLE_MEDIUM);

    }

    public void hardButtonPressed(View v){

        hardButton.setAlpha(1);
        mediumButton.setAlpha(.5f);
        easyButton.setAlpha(.5f);

        Bundle bundle = new Bundle();
        bundle.putString(TABLE_NAME_KEY, DataBase.DB_TABLE_HARD);

        fragment = new TopScoreFragment();
        fragment.setArguments(bundle);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.high_score_layout_id, fragment);
        transaction.commit();

        showAllMarkersOnMap(DataBase.DB_TABLE_HARD);
    }



    private void showAllMarkersOnMap(String tableName) {
        gMap.clear();
        gMap.animateCamera(CameraUpdateFactory.zoomTo(0f));
        if (db.getTableSize(tableName) <= 0 )
            return;

        mAllRecords = db.getAllRecordsFromTable(tableName);
        for(TopScore hs : mAllRecords){
            addPlayerMarkerOnMap(hs);
        }


    }

    private void addPlayerMarkerOnMap(TopScore hs) {
        double lat = hs.getLatitude();
        double lng = hs.getLongtitude();

        if (lat != 0 && lng != 0) {
            LatLng playerPosition = new LatLng(lat, lng);
            gMap.addMarker(new MarkerOptions().position(playerPosition).title("name:" + hs.getName()).snippet("score: " + hs.getScore()));
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        showAllMarkersOnMap(DEFAULT_TABLE_NAME);

    }


    public void getRecordFromClickedList(TopScore hs) {

        gMap.clear();
        addPlayerMarkerOnMap(hs);
        double lat =  hs.getLatitude();
        double lng = hs.getLongtitude();
        LatLng position = new LatLng(lat,lng);
        if (lat != 0 && lng !=0){
            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 10f));

        }


    }
}


