package com.example.dani_pc.bobo;

import android.Manifest;
import android.app.*;
import android.content.*;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.*;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import java.util.Random;

import tyrantgit.explosionfield.ExplosionField;


/**
 * Created by Dani & Simon on 3/12/18.
 *        311315022 204595458
 *
 */

@SuppressWarnings( "ALL" )
public class GameController extends AppCompatActivity implements Services.DevicePositionChangedListener {


    private Intent serviceIntent;
    private Services.LocalBinder binder;
    private boolean isBound = false;

    private double[] coordinates;
    private LocationManager locationManager;
    LocationListener locationListener;
    private int untouchedTiles;
    private int numberOfTurns = 0;


    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (Services.LocalBinder) service;
            binder.registerListener(GameController.this);
            isBound = true;
            binder.getService().initSensorService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
    }




    ExplosionField explosionField;

    private int matrixSize = 10;
    private Game game;
    private int boatDrawSize;
    private String difficultyChosen;

    private void setMatrixSize(String difficultyLevel){

        Log.v("difficultyLevel",difficultyLevel);
        switch (difficultyLevel){
            case "Easy":
                matrixSize = 8;
                break;


            case "Medium":
                matrixSize = 9;
                break;


            case "Hard":
                matrixSize = 10;
                break;
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        savedInstanceState = getIntent().getExtras();
        difficultyChosen = savedInstanceState.getString("difficultyChosen");
        setMatrixSize(difficultyChosen);
        game = new Game(matrixSize);
        placeBoatsView();   // By default, this activity will always display until an event occurs.

    }

    //////////////////////////////






    //////////////////////////////



    private void placeBoatsView() {
        setContentView(R.layout.activity_human_place_boats);
        ImageView aircraft = (ImageView) findViewById(R.id.aircraft);
        ImageView battleship = (ImageView) findViewById(R.id.battleship);
        ImageView destroyer = (ImageView) findViewById(R.id.destroyer);
        ImageView submarine = (ImageView) findViewById(R.id.submarine);
        ImageView patrol = (ImageView) findViewById(R.id.patrol);
        game.getPlayer1Board().boardView = (BoardView) findViewById(R.id.humanBoardView);
        game.getPlayer1Board().boardView.setBoard(game.getPlayer1Board());

        // This means the user had already placed boats on the grid but decided to go back to this view and perhaps
        // change the boats.
        if (game.getPlayer1Board().grid != null) {
                game.getPlayer1Board().boardView.coordinatesOfPlayer1Ships = game.getPlayer1Board().grid;
                game.getPlayer1Board().boardView.invalidate();
        }

        /* Allow these boat images to be draggable, listen when they are touched */
        aircraft.setOnTouchListener(new MyTouchListener());
        battleship.setOnTouchListener(new MyTouchListener());
        destroyer.setOnTouchListener(new MyTouchListener());
        submarine.setOnTouchListener(new MyTouchListener());
        patrol.setOnTouchListener(new MyTouchListener());

        /* Define the particular location where these boat images are allowed to be dragged onto */
        findViewById(R.id.humanBoardPlacer).setOnDragListener(new MyDragListener());

        Button next = (Button) findViewById(R.id.next); // Advance to the next view
        Button random = (Button) findViewById(R.id.random); // Place boats at random
        next.setVisibility(View.VISIBLE);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (game.getPlayer1Board().playerPlacedAllBoats()) {
                        playGameView();
                    } else {
                    toast("You must place all boats before starting the game.");
                }
            }

        });
        random.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                game.getPlayer1Board().clearGrid();
                game.getPlayer1Board().defaultSettings();
                playGameView();
            }
        });
    }

    private void playGameView() {
        explosionField = ExplosionField.attach2Window(this);
        setContentView(R.layout.current_game);
        final Context activityContext = this;


        /////////////////////////////////////////////////////////////////////////////////////////////
        coordinates = new double[2];


        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                if (location != null)
                    setCoordinates(location.getLatitude(), location.getLongitude());
                else
                    setCoordinates(0, 0);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        checkLocationPermission();


        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        }

        else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }


        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);





        /* Define Player's 1 Board */
        game.getPlayer1Board().boardView = (BoardView) findViewById(R.id.humanBoard);
        game.getPlayer1Board().boardView.setBoard(game.getPlayer1Board());
        // And then draw its boats accordingly, so Player 1 can visually see their current boats //
        game.getPlayer1Board().boardView.coordinatesOfPlayer1Ships = game.getPlayer1Board().readBoatCoordinates();

        /* Define Player's 2 Board */
        game.getPlayer2Board().boardView = (BoardView) findViewById(R.id.computerBoard);
        game.getPlayer2Board().boardView.setBoard(game.getPlayer2Board());

        // Define buttons and text views here
        TextView currentPlayerName = (TextView) findViewById(R.id.currentPlayerName);
        TextView opponentsName = (TextView) findViewById(R.id.opponentsName);
        TextView battleshipTitle = (TextView) findViewById(R.id.BattleShip);
        Button newButton = (Button) findViewById(R.id.newButton);
        Button quitButton = (Button) findViewById(R.id.quitButton);

        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveTaskToBack(true);
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
            }
        });

        currentPlayerName.setText(game.getPlayer1Board().getTypeOfPlayer());
        opponentsName.setText(game.getPlayer2Board().getTypeOfPlayer());


        // The predefined methods that allow the user to quit or start a new game
        newActivity(newButton, activityContext);

        toast("Player " + game.getPlayer1Board().getTypeOfPlayer() + " tap " + game.getPlayer2Board()
                .getTypeOfPlayer() + "'s board to shoot!");

        // Handles the instance where the player1 touches player2's board.
        game.getPlayer2Board().boardView.addBoardTouchListener(new BoardView.BoardTouchListener() {
            /* After player taps on computers board */
            @Override
            public void onTouch(int x, int y) {

                    // PLAYER 1 SHOOTS AT COMPUTERS BOARD

                    if(game.getPlayer2Board().grid[x][y] != -1){

                        if (game.shootsAt(game.getPlayer2Board(), x, y)) { // Human hits a boat, paint red

                            Rect testBounds;
                            testBounds = BoardView.getRectExpBounds();
                            //draw explosion to board

                            Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.explosion);
                            explosionField.explode(icon,testBounds,0,2000);





                            toast("HIT");
                        } else { // Human misses, paint computers board white
                            toast("MISS");
                        }
                        numberOfTurns++;
                        checkGameOver();

                        // COMPUTER SHOOTS AT PLAYER'S 1 BOARD
                        boolean flagPC = true;
                        int randomX = 0;
                        int randomY = 0;
                        while(flagPC) {
                            randomX = generateRandomCoordinate(); // Generate random coordinates
                            Log.v("randomX: " ,"" + randomX);
                            randomY = generateRandomCoordinate(); // Generate random coordinates
                            Log.v("randomY: " ,"" + randomY);
                            if(game.getPlayer1Board().grid[randomX][randomY] != -1)
                                flagPC = false;

                        }
                        flagPC = true;
                        if (game.shootsAt(game.getPlayer1Board(), randomX, randomY)) {
                            toast("Your boat has been shot!");
                        }
                        checkGameOver();
                    }else{
                        toast("Invalid Target");
                    }
            }
        });
    }

    /////////////////////////////////////////////////////////////////////////////////////////////

    private void checkLocationPermission() {
        if (Build.VERSION.SDK_INT < 23) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (lastKnownLocation != null)
                    setCoordinates(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());

            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        serviceIntent = new Intent(this, Services.class);
        bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE);
        isBound = true;
    }

    private void setCoordinates(double lat, double lng) {
        coordinates[0] = lat;
        coordinates[1] = lng;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(isBound){
            binder.removeListeners();
            this.stopService(serviceIntent);
            this.unbindService(mConnection);
            isBound = false;
        }


    }


    @Override
    protected void onPause() {
        super.onPause();
        if (isBound){
            binder.removeListeners();
            this.stopService(serviceIntent);
            this.unbindService(mConnection);
            isBound = false;
        }

    }


    @Override
    protected void onStop() {
        super.onStop();
        if(isBound){
            binder.removeListeners();
            this.stopService(serviceIntent);
            this.unbindService(mConnection);
            isBound = false;
        }

    }


    @Override
    protected void onResume() {
        super.onResume();

        if (!isBound) {
            bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE);
            isBound = true;
        }
    }

    @Override
    public void devicePositionChanged() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please Return To Portrait Mode To Keep Playing")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }


    /////////////////////////////////////////////////////////////////////////////////////////////

    private void checkGameOver(){
        if(game.isGameOver()){
            // Game has ended display what player won.
            Bundle endBundle = new Bundle();
            endBundle.putString("theWinner",game.getWinner());
            endBundle.putString("difficultyChosen",difficultyChosen);
            endBundle.putDoubleArray("locationArray", coordinates);

            untouchedTiles = matrixSize * matrixSize - numberOfTurns;
            int finalScore = untouchedTiles * 100;
            endBundle.putInt("finalScore", finalScore);

            Intent goToEndGame = new Intent(GameController.this, EndGame.class);
            goToEndGame.putExtras(endBundle);
            startActivity(goToEndGame);
        }
    }
    private int generateRandomCoordinate() {
        Random random = new Random();
        return random.nextInt(matrixSize);
    }

    private void toast(String msg) {
        final Toast toast = Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT);
        toast.show();
    }

    public void restartActivity() {
        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
    }


    private void newActivity(Button newButton, final Context context) {
        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Alert Dialogue
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Are you sure you want to start a new GameController?");
                builder.setCancelable(true);
                builder.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                toast("New game is successfully created!");
                                restartActivity();
                            }
                        });

                builder.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }



private class MyDragListener implements View.OnDragListener {
        Drawable accept = getResources().getDrawable(R.drawable.accept);
        Drawable reject = getResources().getDrawable(R.drawable.reject);
        Drawable neutral = getResources().getDrawable(R.drawable.neutral);
        Drawable board_color = getResources().getDrawable(R.drawable.board_color);
        // Make images smaller
        int width = 100;
        int height = 100;
        LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(width, height);
        private boolean getResult = false; // Determine if the an image object has been dragged
        private int tempX = 0;
        private int tempY = 0;

        @Override
        public boolean onDrag(View v, DragEvent event) {
            v.setBackground(board_color);
            switch (event.getAction()) {

                case DragEvent.ACTION_DRAG_STARTED:
                    v.setBackgroundDrawable(neutral);
                    v.setBackground(board_color);
                    break;

                case DragEvent.ACTION_DRAG_ENTERED:

                    v.setBackgroundDrawable(neutral);
                    v.setBackground(board_color);
                    break;

                case DragEvent.ACTION_DRAG_EXITED:

                    v.setBackground(board_color);

                    break;
                case DragEvent.ACTION_DROP:

                    v.setBackgroundDrawable(accept);
                    getResult = true;
                    int[][] boatsCoordinates = new int[matrixSize][matrixSize];

                    View view = (View) event.getLocalState(); // Current image
                    ViewGroup owner = (ViewGroup) view.getParent(); // RelativeLayout id: humanBoardPlacer
                    view.setLayoutParams(parms);
                    owner.removeView(view);                         // Remove the current image from the humanBoardPlacer
                    RelativeLayout container = (RelativeLayout) v; // Container for boardView

                    String boatWeAreDragging = getResources().getResourceEntryName(view.getId());
                    // Round to the nearest 50
                    int convertX = (int) event.getX();
                    int convertY = (int) event.getY();
                    BoardView b = (BoardView)findViewById(R.id.humanBoardView);
                    int brickSize = b.getWidth() / 10;

                    switch(boatWeAreDragging){
                        case "aircraft":
                            boatDrawSize = 5 * brickSize;
                            break;
                        case "battleship":
                            boatDrawSize = 4 * brickSize;
                            break;
                        case "destroyer":
                            boatDrawSize = 3 * brickSize;
                            break;
                        case "submarine":
                            boatDrawSize = 3 * brickSize;
                            break;
                        case "patrol":
                            boatDrawSize = 2 * brickSize;
                            break;
                    }

                    if (convertX < b.getWidth() -boatDrawSize + 50 && convertY <= b.getHeight()) {
                        // Place boat at the dragged coordinate
                        Log.w("round X", String.valueOf(convertX) + "round Y" + String.valueOf(convertY));
                        view.setX(convertX);
                        view.setY(convertY);

                        // Store the coordinates to a temp variable in case the user places the boat out of bounds
                        tempX = convertX;
                        tempY = convertY;

                        container.addView(view);
                        view.setVisibility(View.VISIBLE);

                        switch (boatWeAreDragging) {
                            case "aircraft":
                                if (game.getPlayer1Board().aircraft.isPlaced()) { // Boat has already been placed
                                    game.getPlayer1Board().removeCoordinates(game.getPlayer1Board().aircraft.map,boatWeAreDragging);
                                    // Delete all coordinates for this ship
                                    game.getPlayer1Board().aircraft.clearCoordinates();
                                }

                                tempX = game.getPlayer1Board().boardView.locateX(convertX);
                                tempY = game.getPlayer1Board().boardView.locateY(convertY);

                                for (int i = 0; i < 5; i++) {
                                    if (tempX + i >= 0 && tempX + i < matrixSize && tempY < matrixSize && tempY >= 0) {
                                        boatsCoordinates[tempX + i][tempY] = 1;
                                    }
                                }
                                game.getPlayer1Board().aircraft.map = boatsCoordinates;

                                if(game.getPlayer1Board().isLocationFull(game.getPlayer1Board().aircraft.map)){
                                    toast("There is already a boat in place");
                                    game.getPlayer1Board().aircraft.setPlaced(false);
                                }
                                else {
                                    game.getPlayer1Board().addBoatToGrid(game.getPlayer1Board().aircraft.map);
                                    game.getPlayer1Board().aircraft.setPlaced(true);
                                }
                                break;

                            case "battleship":
                                if (game.getPlayer1Board().battleship.isPlaced()) { // If boat is already placed
                                    game.getPlayer1Board().removeCoordinates(game.getPlayer1Board().battleship.map,boatWeAreDragging);
                                    // Delete all coordinates for this ship
                                    game.getPlayer1Board().battleship.clearCoordinates();
                                }

                                tempX = game.getPlayer1Board().boardView.locateX(convertX);
                                tempY = game.getPlayer1Board().boardView.locateY(convertY);


                                for (int i = 0; i < 4; i++) {
                                    if (tempX + i >= 0 && tempX + i < matrixSize && tempY < matrixSize && tempY >= 0) {
                                        boatsCoordinates[tempX + i][tempY] = 2;
                                    }
                                }

                                game.getPlayer1Board().battleship.map = boatsCoordinates;

                                if(game.getPlayer1Board().isLocationFull(game.getPlayer1Board().battleship.map)){
                                    toast("There is already a boat in place");
                                    game.getPlayer1Board().battleship.setPlaced(false);
                                }
                                else {
                                    game.getPlayer1Board().addBoatToGrid(game.getPlayer1Board().battleship.map);
                                    game.getPlayer1Board().battleship.setPlaced(true);
                                }
                                break;


                            case "destroyer":
                                if (game.getPlayer1Board().destroyer.isPlaced()) { // If boat is already placed
                                    game.getPlayer1Board().removeCoordinates(game.getPlayer1Board().destroyer.map,boatWeAreDragging);
                                    // Delete all coordinates for this ship
                                    game.getPlayer1Board().destroyer.clearCoordinates();
                                }

                                tempX = game.getPlayer1Board().boardView.locateX(convertX);
                                tempY = game.getPlayer1Board().boardView.locateY(convertY);

                                for (int i = 0; i < 3; i++) {
                                    if (tempX + i >= 0 && tempX + i < matrixSize && tempY < matrixSize && tempY >= 0) {
                                        boatsCoordinates[tempX + i][tempY] = 3;
                                    }
                                }
                                game.getPlayer1Board().destroyer.map = boatsCoordinates;

                                if(game.getPlayer1Board().isLocationFull(game.getPlayer1Board().destroyer.map)){
                                    toast("There is already a boat in place");
                                    game.getPlayer1Board().destroyer.setPlaced(false);
                                }
                                else {
                                    game.getPlayer1Board().addBoatToGrid(game.getPlayer1Board().destroyer.map);
                                    game.getPlayer1Board().destroyer.setPlaced(true);
                                }
                                break;
                            case "submarine":
                                if (game.getPlayer1Board().submarine.isPlaced()) { // If boat is already placed
                                    game.getPlayer1Board().removeCoordinates(game.getPlayer1Board().submarine.map,boatWeAreDragging);
                                    //Delete all coordinates for this ship
                                    game.getPlayer1Board().submarine.clearCoordinates();
                                }

                                tempX = game.getPlayer1Board().boardView.locateX(convertX);
                                tempY = game.getPlayer1Board().boardView.locateY(convertY);

                                for (int i = 0; i < 3; i++) {
                                    if (tempX + i >= 0 && tempX + i < matrixSize && tempY < matrixSize && tempY >= 0) {
                                        boatsCoordinates[tempX + i][tempY] = 4;
                                    }
                                }
                                game.getPlayer1Board().submarine.map = boatsCoordinates;

                                if(game.getPlayer1Board().isLocationFull(game.getPlayer1Board().submarine.map)){
                                    toast("There is already a boat in place");
                                    game.getPlayer1Board().submarine.setPlaced(false);
                                }
                                else {
                                    game.getPlayer1Board().addBoatToGrid(game.getPlayer1Board().submarine.map);
                                    game.getPlayer1Board().submarine.setPlaced(true);
                                }
                                break;

                            case "patrol":
                                if (game.getPlayer1Board().patrol.isPlaced()) { // If boat is already placed
                                    game.getPlayer1Board().removeCoordinates(game.getPlayer1Board().patrol.map,boatWeAreDragging);
                                    game.getPlayer1Board().patrol.clearCoordinates(); // Delete all coordinates for this ship
                                }

                                tempX = game.getPlayer1Board().boardView.locateX(convertX);
                                tempY = game.getPlayer1Board().boardView.locateY(convertY);

                                for (int i = 0; i < 2; i++) {
                                    if (tempX + i >= 0 && tempX + i < matrixSize && tempY < matrixSize && tempY >= 0) {
                                        boatsCoordinates[tempX + i][tempY] = 5;
                                    }
                                }
                                game.getPlayer1Board().patrol.map = boatsCoordinates;

                                if(game.getPlayer1Board().isLocationFull(game.getPlayer1Board().patrol.map)){
                                    toast("There is already a boat in place");
                                    game.getPlayer1Board().patrol.setPlaced(false);
                                }
                                else {
                                    game.getPlayer1Board().addBoatToGrid(game.getPlayer1Board().patrol.map);
                                    game.getPlayer1Board().patrol.setPlaced(true);
                                }
                                break;
                        }

                    } else { // OUT OF BOUNDS, RESET THE BOAT COORDINATES TO PREVIOUS LOCATION
                        v.setBackgroundDrawable(reject);
                        container.addView(view);
                        view.setVisibility(View.VISIBLE);
                        toast("Out of bounds");
                        getResult = true; //Changed From True - he had true we changed to false

                    }

                    break;

                case DragEvent.ACTION_DRAG_ENDED:
                   // if (!getResult) {
                  //      toast("Can't place here");
                  //  }
                    view = (View) event.getLocalState();
                    view.setVisibility(View.VISIBLE);
                default:
                    game.getPlayer1Board().boardView.coordinatesOfPlayer1Ships = game.getPlayer1Board().grid; // Prevents from drawing multiple times when the
                    // user changes

                    game.getPlayer1Board().boardView.invalidate();
                    break;
            }

            return true;
        }
    }

    private class MyTouchListener implements View.OnTouchListener {
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                view.startDragAndDrop(data, shadowBuilder, view, 0);
                view.setVisibility(View.INVISIBLE);
                return true;
            } else {
                return false;
            }
        }
    }
}

