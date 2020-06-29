package com.example.dani_pc.bobo;

public class Game {

    private Board player1Board;
    private Board player2Board;
    private boolean isGameOver = false;
    private int matrixSize = 10;
    private String theWinner;


    Game(int size) {

        setMatrixSize(size);
        Board playerBoard = new Board(matrixSize, "Human");
        Board computerBoard = new Board(matrixSize, "Computer");
        setPlayer1Board(playerBoard);
        setPlayer2Board(computerBoard);

    }

    public void setMatrixSize(int size){
        matrixSize = size;
    }

    boolean shootsAt(Board opponentsBoard, int x, int y) {
        if (opponentsBoard.grid[x][y] != -1) {
            int boatID = opponentsBoard.grid[x][y];
            opponentsBoard.grid[x][y] = -1;
            switch (boatID) {
                case 1:  // Shot aircraft
                    opponentsBoard.addBoatToGrid(opponentsBoard.aircraft.map);
                    opponentsBoard.aircraft.addCoordinates(x, y);
                    opponentsBoard.boardView.gameCoordinates[x][y] = 8; // Set it to 8 to indicate it is a hit
                    opponentsBoard.boardView.invalidate();
                    return true;

                case 2:  // Shot battleship
                    opponentsBoard.addBoatToGrid(opponentsBoard.battleship.map);
                    opponentsBoard.battleship.addCoordinates(x, y);
                    opponentsBoard.boardView.gameCoordinates[x][y] = 8; // Set it to 8 to indicate it is a hit
                    opponentsBoard.boardView.invalidate();
                    return true;

                case 3:  // Shot destroyer
                    opponentsBoard.addBoatToGrid(opponentsBoard.destroyer.map);
                    opponentsBoard.destroyer.addCoordinates(x, y);
                    opponentsBoard.boardView.gameCoordinates[x][y] = 8; // Set it to 8 to indicate it is a hit
                    opponentsBoard.boardView.invalidate();
                    return true;

                case 4:  // Shot submarine
                    opponentsBoard.addBoatToGrid(opponentsBoard.submarine.map);
                    opponentsBoard.submarine.addCoordinates(x, y);
                    opponentsBoard.boardView.gameCoordinates[x][y] = 8; // Set it to 8 to indicate it is a hit
                    opponentsBoard.boardView.invalidate();
                    return true;

                case 5:  // Shot patrol
                    opponentsBoard.addBoatToGrid(opponentsBoard.patrol.map);
                    opponentsBoard.patrol.addCoordinates(x, y);
                    opponentsBoard.boardView.gameCoordinates[x][y] = 8; // Set it to 8 to indicate it is a hit
                    opponentsBoard.boardView.invalidate();
                    return true;

                default:
                    opponentsBoard.boardView.gameCoordinates[x][y] = -9; // Set it to -9 to indicate it is a miss
                    opponentsBoard.boardView.invalidate();
                    return false;
            }

        }

        return false;
    }


    public Board getPlayer1Board() {
        return player1Board;
    }

    public void setPlayer1Board(Board player1Board) {
        this.player1Board = player1Board;
    }

    public Board getPlayer2Board() {
        return player2Board;
    }

    public void setPlayer2Board(Board player2Board) {
        this.player2Board = player2Board;
    }

    public boolean isGameOver() {
        boolean temp = true;
        for (int i = 0; i < player1Board.grid.length && temp; i++) {
            for (int j = 0; j < player1Board.grid[0].length && temp; j++) {
                if (player1Board.grid[i][j] != -1 && player1Board.grid[i][j] != 0) {
                    temp = false;
                }
            }
        }
        isGameOver = temp;
        if(isGameOver){
            theWinner = "Computer";
            return isGameOver;
        }
        theWinner = "Player";
        for(int i = 0; i < player2Board.grid.length; i ++){
            for(int j = 0; j < player2Board.grid[0].length;j ++){
                if(player2Board.grid[i][j] != -1 && player2Board.grid[i][j] != 0){
                    return isGameOver;
                }
            }
        }
        isGameOver = true;
        return isGameOver;
    }

    public String getWinner(){
        return this.theWinner;
    }

}
