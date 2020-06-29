package com.example.dani_pc.bobo;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class EndGame extends AppCompatActivity {
    private MediaPlayer mp;
    private String winnerForMp;
    private EditText newRecord;
    private String player;
    private TopScore topScore;
    private double[] playerCoordinates;
    private DataBase db;
    private String difficultyChosen;
    private int finalScore;
    private int lowestScore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_ended);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        TextView endTv = findViewById(R.id.winner);
        playerCoordinates = new double[2];

        savedInstanceState = getIntent().getExtras();
        if (savedInstanceState!= null) {
            winnerForMp = savedInstanceState.getString("theWinner");

            difficultyChosen = savedInstanceState.getString("difficultyChosen");
            playerCoordinates = savedInstanceState.getDoubleArray("locationArray");
            finalScore = savedInstanceState.getInt("finalScore");
        }

        endTv.setText(winnerForMp);

        if(winnerForMp.equals("Player")) {
            mp = MediaPlayer.create(this, R.raw.anthem);
            db = DataBase.getInstance(this);

            if (db.isTableFull(difficultyChosen)){
                lowestScore = db.getWorstRecord(difficultyChosen).getScore();
                if (finalScore > lowestScore){
                    //delete worst record in table
                    db.deleteWorstRecord(difficultyChosen);
                }
            }
            new CountDownTimer(1500, 1000) {

                public void onTick(long millisUntilFinished) {

                }

                public void onFinish() {
                    createNewRecordDialog();
                }
            }.start();

        }

        else {
                mp = MediaPlayer.create(this, R.raw.titanic);
        }

        mp.start();

        Button playAgainBtn = findViewById(R.id.restartButton);
        Button quitGameBtn = findViewById(R.id.quitGameButton);

        playAgainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.stop();
                Intent newGameIntent = new Intent(EndGame.this, MainActivity.class);
                startActivity(newGameIntent);

            }
        });

        quitGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.stop();
                moveTaskToBack(true);
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
            }
        });






    }


    public void createNewRecordDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(EndGame.this);
        builder.setTitle("NEW RECORD!");
        builder.setMessage("Congrats! you set a new Record!");

        newRecord = new EditText(this);
        newRecord.setHint(R.string.record_name);

        builder.setView(newRecord);
        builder.setPositiveButton("SUBMIT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                player = newRecord.getText().toString();

                if (newRecord.length()!=0) {
                    topScore = new TopScore(player, finalScore, playerCoordinates[0], playerCoordinates[1]);
                    db.insertRecord(difficultyChosen, topScore);
                }

            }
        });
        AlertDialog alert = builder.create();
        alert.show();

    }


}
