package com.example.dani_pc.bobo;

public class Board {

    private final int size;
    private int matrixSize;
    protected int[][] grid;
    private String typeOfPlayer;
    BoardView boardView;
    Ship aircraft;
    Ship battleship;
    Ship destroyer;
    Ship submarine;
    Ship patrol;



    public Board(int size, String player) {
        setMatrixSize(size);
        grid = new int[matrixSize][matrixSize];
        for(int i = 0; i < grid.length; i++){
            for(int j=0; j < grid[i].length; j++){
                grid[i][j] = 0;
            }
        }

        aircraft = new Ship(5, "aircraft",matrixSize);
        battleship = new Ship(4, "battleship",matrixSize);
        destroyer = new Ship(3, "destroyer",matrixSize);
        submarine = new Ship(3, "submarine",matrixSize);
        patrol = new Ship(2, "patrol",matrixSize);
        this.size = size;
        if (player.equals("Human")) {
            setTypeOfPlayer("Human");
        }
        if (player.equals("Computer")) {
            setTypeOfPlayer("Computer");
            // Add random coordinates for a specific boat to the players board grid
            defaultSettings();
        }

    }

    public void setMatrixSize(int size){
        matrixSize = size;
    }



    public void defaultSettings() {
        int [][] tempMap = new int [matrixSize][matrixSize];
        boolean taken = true;
        addBoatToGrid(aircraft.randomCoordinates());
        aircraft.setPlaced(true);
        while(taken) {
            tempMap = battleship.randomCoordinates();
            taken = checkCollision(tempMap, aircraft.map);
        }
        addBoatToGrid(tempMap);
        battleship.setPlaced(true);

        taken = true;
        while(taken) {
            tempMap = destroyer.randomCoordinates();
            taken = checkCollision(tempMap, aircraft.map);
            if(!taken)
                taken = checkCollision(tempMap, battleship.map);
        }
        addBoatToGrid(tempMap);
        destroyer.setPlaced(true);

        taken = true;
        while(taken) {
            tempMap = submarine.randomCoordinates();
            taken = checkCollision(tempMap, aircraft.map);
            if(!taken) {
                taken = checkCollision(tempMap, battleship.map);
                if (!taken)
                    taken = checkCollision(tempMap, destroyer.map);
            }
        }
        addBoatToGrid(tempMap);
        submarine.setPlaced(true);

        taken = true;
        while(taken) {
            tempMap = patrol.randomCoordinates();
            taken = checkCollision(tempMap, aircraft.map);
            if(!taken) {
                taken = checkCollision(tempMap, battleship.map);
                if (!taken) {
                    taken = checkCollision(tempMap, destroyer.map);
                    if (!taken)
                        taken = checkCollision(tempMap, submarine.map);
                }
            }
        }
        addBoatToGrid(tempMap);
        patrol.setPlaced(true);
    }

    public boolean checkCollision(int[][] map1, int [][] map2){
        for(int i=0; i < map1.length; i++){
            for(int j=0; j < map2.length; j++){
                if(map1[i][j] != 0 && map2[i][j] != 0){
                   return true;
                }

            }
        }


        return false;
    }
    int[][] readBoatCoordinates() {
        return grid;
    }

    boolean addBoatToGrid(int[][] coordinates) {
        boolean flag = false;
        for (int i = 0; i < coordinates.length && !flag; i++) {
            for (int j = 0; j < coordinates.length && !flag; j++) {
                if (coordinates[i][j] == 1) { // Aircraft ID
                    if(grid[i][j] != 0) {
                        flag = true;

                        break;
                    }
                    grid[i][j] = 1;
                }
                if (coordinates[i][j] == 2) { // Battleship ID
                    if(grid[i][j] != 0) {
                        flag = true;
                        break;
                    }
                    grid[i][j] = 2;
                }
                if (coordinates[i][j] == 3) { // Destroyer ID
                    if(grid[i][j] != 0) {
                        flag = true;
                        break;
                    }
                    grid[i][j] = 3;
                }
                if (coordinates[i][j] == 4) { // Submarine ID
                    if(grid[i][j] != 0) {
                        flag = true;
                        break;
                    }
                    grid[i][j] = 4;
                }
                if (coordinates[i][j] == 5) { // Patrol ID
                    if(grid[i][j] != 0) {
                        flag = true;
                        break;
                    }
                    grid[i][j] = 5;
                }
            }
        }
        return flag;
    }




    boolean isLocationFull(int[][] coordinates) {

        for (int i = 0; i < coordinates.length; i++) {
            for (int j = 0; j < coordinates.length; j++) {
                if (coordinates[i][j] != 0 && grid[i][j] != 0) { // Aircraft ID
                        return true;
                }
            }
        }
        return false;
    }



    void removeCoordinates(int[][] coordinates,String shipName) {
        int shipNum = 0;
        switch(shipName) {
            case "aircraft":
                shipNum = 1;
                break;
            case "battleship":
                shipNum = 2;
                break;
            case "destroyer":
                shipNum = 3;
                break;
            case "submarine":
                shipNum = 4;
                break;
            case "patrol":
                shipNum = 5;
                break;
        }
        for (int i = 0; i < coordinates.length; i++) {
            for (int j = 0; j < coordinates.length; j++) {
                if (coordinates[i][j] == shipNum) { // Remove aircraft coordinates
                    grid[i][j] = 0;
                }
            }
        }
    }

    public void clearGrid(){
        for(int i=0; i < grid.length; i++){
            for(int j=0; j < grid[0].length; j++){
                grid[i][j] = 0;
            }
        }
    }



    int size() {
        return size;
    }

    String getTypeOfPlayer() {
        return typeOfPlayer;
    }

    private void setTypeOfPlayer(String typeOfPlayer) {
        this.typeOfPlayer = typeOfPlayer;
    }

    public boolean playerPlacedAllBoats() {
        return aircraft.isPlaced() && battleship.isPlaced() && destroyer.isPlaced() && submarine.isPlaced() && patrol.isPlaced();
    }

}