package com.example.dani_pc.bobo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {


    String difficultyChosen;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);


        Spinner difficultySpinner = (Spinner) findViewById(R.id.difficultySpinner);
        final List<SpinnerData> difficultiesList = new ArrayList<>();
        difficultiesList.add(new SpinnerData(R.drawable.easy_difficulty,"Easy"));
        difficultiesList.add(new SpinnerData(R.drawable.medium_difficulty,"Medium"));
        difficultiesList.add(new SpinnerData(R.drawable.hard_difficulty,"Hard"));

        CustomSpinnerAdapter customSpinnerAdapter = new CustomSpinnerAdapter(MainActivity.this,R.layout.spinner_layout,difficultiesList);
        difficultySpinner.setAdapter(customSpinnerAdapter);

        difficultySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this,"Selected Difficulty: " + difficultiesList.get(position).getIconName(),Toast.LENGTH_SHORT).show();
                difficultyChosen = difficultiesList.get(position).getIconName();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do Nothing
            }
        });


        Button customButton = (Button) findViewById(R.id.mainButton);
        Button topScoresButton = (Button) findViewById(R.id.topScoreButton);

        customButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle b = new Bundle();
                b.putString("difficultyChosen",difficultyChosen);


                Intent goToGameController = new Intent(MainActivity.this, GameController.class);
                goToGameController.putExtras(b);
                startActivity(goToGameController);






            }
        });

        topScoresButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToTopScoreTable();
            }
        });



    }

    public void goToTopScoreTable(){

        Intent goToScores = new Intent(MainActivity.this, TopScoreActivity.class);
        startActivity(goToScores);

    }

}
