package com.example.dani_pc.bobo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;


import java.util.ArrayList;
import java.util.List;

import tyrantgit.explosionfield.ExplosionField;


public class BoardView extends View {



    private static Rect boundsOfExp;

    /**
     * Listeners to be notified upon board touches.
     */
    private final List<BoardTouchListener> listeners = new ArrayList<>();
    /**
     * Board background color.
     */
    private final int boardColor = Color.argb(0, 255, 255, 255); // Transparent
    /**
     * Red color circle
     **/
    private final int redColor = Color.rgb(255, 69, 0);
    /**
     * Black color circle
     **/
    private final int blackColor = Color.rgb(0, 0, 0);
    /**
     * White color circle
     **/
    private final int whiteColor = Color.rgb(255, 255, 255);
    /**
     * Board background paint.
     */
    private final Paint boardPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    /**
     * Red background paint
     */
    private final Paint redPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    /**
     * Black background paint
     */
    private final Paint blackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    /**
     * White background paint
     */
    private final Paint whitePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    /**
     * Board grid line paint.
     */
    private final Paint boardLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    int[][] coordinatesOfPlayer1Ships;
    int[][] gameCoordinates;

    /**
     * Size of the board.
     */
    private int boardSize = 10;


    {
        boardPaint.setColor(boardColor);
    }

    {
        redPaint.setColor(redColor);
    }

    {
        blackPaint.setColor(blackColor);
    }

    {
        whitePaint.setColor(whiteColor);
    }

    {
        /* Board grid line color. */
        int boardLineColor = Color.WHITE;
        boardLinePaint.setColor(boardLineColor);
        boardLinePaint.setStrokeWidth(3);
    }

    /**
     * Create a new board view to be run in the given context.
     */
    public BoardView(Context context) {
        super(context);
    }


    /**
     * Create a new board view with the given attribute set.
     */
    public BoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Create a new board view with the given attribute set and style.
     */
    public BoardView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Set the board to to be displayed by this view.
     */
    void setBoard(Board board) {
        /* Board to be displayed by this view. */
        this.boardSize = board.size();
        coordinatesOfPlayer1Ships = new int [boardSize][boardSize];
        gameCoordinates = new int [boardSize][boardSize];
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                int xy = locatePlace(event.getX(), event.getY());
                invalidate();
                if (xy >= 0) {
                    notifyBoardTouch(xy / 100, xy % 100);
                }
                break;
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return true;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawPlayer1Ships(canvas);
        drawBoard(canvas);
        drawGrid(canvas);
    }

    private void drawPlayer1Ships(Canvas canvas) {
        if (coordinatesOfPlayer1Ships != null) {
            for (int i = 0; i < coordinatesOfPlayer1Ships.length; i++) {
                for (int j = 0; j < coordinatesOfPlayer1Ships.length; j++) {
                    if (coordinatesOfPlayer1Ships[i][j] >= 1) {       // DRAW ALL BOATS HERE
                        float drawX = (i * lineGap()) + (lineGap() / 2);
                        float drawY = (j * lineGap()) + (lineGap() / 2);
                        int left = (int) (drawX - (lineGap() / 2));
                        int top = (int) (drawY - (lineGap() / 2));
                        int right = (int) (drawX + (lineGap() / 2));
                        int bottom = (int) (drawY + (lineGap() / 2));
                        canvas.drawRect(left, top, right, bottom, blackPaint);
                    }
                }
            }
        }
    }

    private void drawBoard(Canvas canvas) {

        Drawable exp = getResources().getDrawable(R.drawable.explosion,null);//


        if (gameCoordinates != null) {
            for (int i = 0; i < gameCoordinates.length; i++) {
                for (int j = 0; j < gameCoordinates.length; j++) {
                    if (gameCoordinates[i][j] == 8) { // HIT
                        float xMiss = (i * lineGap()) + (lineGap() / 2);
                        float yMiss = (j * lineGap()) + (lineGap() / 2);
                        exp.setBounds((int)(xMiss - (lineGap() / 2)),(int)(yMiss - (lineGap() / 2)),(int)(xMiss + (lineGap() / 2)),(int)(yMiss + (lineGap() / 2))); //mine
                        boundsOfExp = new Rect((int)(xMiss - (lineGap() / 2)),(int)(yMiss - (lineGap() / 2)),(int)(xMiss + (lineGap() / 2)),(int)(yMiss + (lineGap() / 2)));//MINE

                        //canvas.drawCircle(xMiss, yMiss, (lineGap() / 2), redPaint);// HIT
                        /*
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        */
                        exp.draw(canvas);

                    }
                    if (gameCoordinates[i][j] == -9) { // MISS
                        float xMiss = (i * lineGap()) + (lineGap() / 2);
                        float yMiss = (j * lineGap()) + (lineGap() / 2);
                        /*
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        */
                        canvas.drawCircle(xMiss, yMiss, (lineGap() / 2), whitePaint);  // MISS
                    }
                }
            }
        }
    }

    public static Rect getRectExpBounds(){ //MINE
        return boundsOfExp;
    }


    private void drawGrid(Canvas canvas) {
        final float maxCoord = maxCoord();
        final float placeSize = lineGap();
        canvas.drawRect(0, 0, maxCoord, maxCoord, boardPaint);
        for (int i = 0; i < numOfLines(); i++) {
            float xy = i * placeSize;
            canvas.drawLine(0, xy, maxCoord, xy, boardLinePaint); // horizontal line
            canvas.drawLine(xy, 0, xy, maxCoord, boardLinePaint); // vertical line
        }
    }


    private float lineGap() {
        return Math.min(getMeasuredWidth(), getMeasuredHeight()) / (float) boardSize;
    }


    private int numOfLines() {
        return boardSize + 1;
    }

    private float maxCoord() {
        return lineGap() * (numOfLines() - 1);
    }

    private int locatePlace(float x, float y) {
        if (x <= maxCoord() && y <= maxCoord()) {
            final float placeSize = lineGap();
            int ix = (int) (x / placeSize);
            int iy = (int) (y / placeSize);
            Log.w("(ix, iy)", String.valueOf(ix) + "," + String.valueOf(iy));
            return ix * 100 + iy;
        }
        return -1;
    }

    int locateX(float x) {
        if (x <= maxCoord()) {
            final float placeSize = lineGap();
            return (int) (x / placeSize);
        }
        return -1;
    }

    int locateY(float y) {
        return locateX(y);
    }

    void addBoardTouchListener(BoardTouchListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }


    private void notifyBoardTouch(int x, int y) {
        for (BoardTouchListener listener : listeners) {
            listener.onTouch(x, y);
        }
    }


    public interface BoardTouchListener {

        void onTouch(int x, int y);

    }


}