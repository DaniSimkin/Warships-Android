package com.example.dani_pc.bobo;

public class Ship {
    private int matrixSize = 10;
    int[][] map; // size of the grid
    private int size;
    private boolean isPlaced;
    private String nameOfShip;
    private String position;

    Ship(int size, String ship, int matrixSize) {
        this.matrixSize = matrixSize;
        map = new int[this.matrixSize][this.matrixSize];
        setSize(size);
        nameShip(ship);
    }

    void clearCoordinates() {
        // Clear all elements since the user wants to place boat elsewhere
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map.length; j++) {
                map[i][j] = 0;
            }
        }
    }

    //The Computer Uses This Func
    int[][] randomCoordinates() {

        clearCoordinates();
        int direction = (int) (Math.random() * 2);
        int coordinatesRange = (map.length - getSizeOfBoat());
        int randomX = 0;
        int randomY = 0;

        randomX = (int) (Math.random() * coordinatesRange);
        randomY = (int) (Math.random() * coordinatesRange);

        if (direction == 1) { // if boat is horizontal
            setPosition("horizontal");


            for (int i = 0; i < getSizeOfBoat(); i++) { // place boat horizontal
                if (getNameOfShip().equals("aircraft")) {
                    map[randomX][randomY + i] = 1; // Adding to the right of the head
                }
                if (getNameOfShip().equals("battleship")) {
                    map[randomX][randomY + i] = 2; // Adding to the right of the head
                }
                if (getNameOfShip().equals("destroyer")) {
                    map[randomX][randomY + i] = 3; // Adding to the right of the head
                }
                if (getNameOfShip().equals("submarine")) {
                    map[randomX][randomY + i] = 4; // Adding to the right of the head
                }
                if (getNameOfShip().equals("patrol")) {
                    map[randomX][randomY + i] = 5; // Adding to the right of the head
                }
            }
        } else {
            setPosition("vertical");


            for (int j = 0; j < getSizeOfBoat(); j++) { // place boat vertical
                if (getNameOfShip().equals("aircraft")) {
                    map[randomX + j][randomY] = 1; // Adding below of the head
                }
                if (getNameOfShip().equals("battleship")) {
                    map[randomX + j][randomY] = 2; // Adding below of the head
                }
                if (getNameOfShip().equals("destroyer")) {
                    map[randomX + j][randomY] = 3; // Adding below of the head
                }
                if (getNameOfShip().equals("submarine")) {
                    map[randomX + j][randomY] = 4; // Adding below of the head
                }
                if (getNameOfShip().equals("patrol")) {
                    map[randomX + j][randomY] = 5; // Adding below of the head
                }
            }
        }
        return map;
    }


    private int getSizeOfBoat() {
        return size;
    }

    private void setSize(int size) {
        this.size = size;
    }

    private void setPosition(String position) {
        this.position = position;
    }

    boolean isPlaced() {
        return isPlaced;
    }

    void setPlaced(boolean placed) {
        isPlaced = placed;
    }

    String getNameOfShip() {
        return nameOfShip;
    }

    private void nameShip(String nameOfShip) {
        this.nameOfShip = nameOfShip;
    }


    public void addCoordinates(int x, int y) {
        switch(getNameOfShip()){
            case "aircraft":
                map[x][y] = 1;
                break;

            case "battleship":
                map[x][y] = 2;
                break;

            case "destroyer":
                map[x][y] = 3;
                break;

            case "submarine":
                map[x][y] = 4;
                break;

            case "patrol":
                map[x][y] = 5;
                break;


        }



    }
}
