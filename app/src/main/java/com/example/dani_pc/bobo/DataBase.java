package com.example.dani_pc.bobo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DataBase extends SQLiteOpenHelper {

    private static DataBase mInstance = null;

    public static final  int DB_VERSION = 1;

    public static final  String DB_NAME = "Highscores.db";

    public static final String DB_TABLE_EASY = "EASY";
    public static final String DB_TABLE_MEDIUM = "MEDIUM";
    public static final String DB_TABLE_HARD = "HARD";

    private static final String DB_PLAYER_ID = "id";
    private static final String DB_PLAYER_NAME = "name";
    private static final String DB_PLAYER_SCORE = "score";
    private static final String DB_PLAYER_LAT = "latitude";
    private static final String DB_PLAYER_LONG = "longitude";

    private static final int DB_PLAYER_ID_INDEX = 0;
    private static final int DB_PLAYER_NAME_INDEX = 1;
    private static final int DB_PLAYER_SCORE_INDEX = 2;
    private static final int DB_PLAYER_LAT_INDEX = 3;
    private static final int DB_PLAYER_LONG_INDEX = 4;

    private final static int MAX_RECORDS = 10;


    private DataBase(Context context){
        super(context,DB_NAME,null,DB_VERSION);
    }

    public static synchronized DataBase getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DataBase(context.getApplicationContext());
        }
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_TABLE_EASY = "CREATE TABLE " + DB_TABLE_EASY + "(" +
                DB_PLAYER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                DB_PLAYER_NAME + " TEXT," +
                DB_PLAYER_SCORE + " INTEGER," +
                DB_PLAYER_LAT + " DOUBLE," +
                DB_PLAYER_LONG + " DOUBLE " +");";

        String CREATE_TABLE_MEDIUM = "CREATE TABLE " + DB_TABLE_MEDIUM + "(" +
                DB_PLAYER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                DB_PLAYER_NAME + " TEXT," +
                DB_PLAYER_SCORE + " INTEGER," +
                DB_PLAYER_LAT + " DOUBLE," +
                DB_PLAYER_LONG + " DOUBLE " +");";

        String CREATE_TABLE_HARD = "CREATE TABLE " + DB_TABLE_HARD + "(" +
                DB_PLAYER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                DB_PLAYER_NAME + " TEXT," +
                DB_PLAYER_SCORE + " INTEGER," +
                DB_PLAYER_LAT + " DOUBLE," +
                DB_PLAYER_LONG + " DOUBLE " +");";

        db.execSQL(CREATE_TABLE_EASY);
        db.execSQL(CREATE_TABLE_MEDIUM);
        db.execSQL(CREATE_TABLE_HARD);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_EASY);
        db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_MEDIUM);
        db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_HARD);

        onCreate(db);

    }



    public void deleteAllTable(String tableName){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ tableName);
    }





    public TopScore getWorstRecord(String difficulty){

        String theQuery = "SELECT * FROM " + difficulty + "ORDER BY " + DB_PLAYER_SCORE + " ASC;" ;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(theQuery,null);

        if (getTableSize(difficulty) == 0){
            return null;
        }

        c.moveToFirst();
        TopScore highScoreRecord = new TopScore(
                c.getInt(DB_PLAYER_ID_INDEX),c.getString(DB_PLAYER_NAME_INDEX),c.getInt(DB_PLAYER_SCORE_INDEX),
                c.getFloat(DB_PLAYER_LAT_INDEX),c.getFloat(DB_PLAYER_LONG_INDEX));
        return highScoreRecord;
    }



    public int getTableSize(String tableName) {

        String theQuery = "SELECT count(*) FROM "+ tableName +";";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(theQuery, null);
        c.moveToFirst();
        return c.getInt(0);

    }



    public boolean isTableFull(String tableName){
        int tableSize = getTableSize(tableName);
        if(tableSize==MAX_RECORDS)
            return true;
        return false;
    }


    public void insertRecord(String tableName, TopScore highScoreRecord) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DB_PLAYER_NAME, highScoreRecord.getName());
        values.put(DB_PLAYER_SCORE, highScoreRecord.getScore());
        values.put(DB_PLAYER_LAT, highScoreRecord.getLatitude());
        values.put(DB_PLAYER_LONG, highScoreRecord.getLongtitude());

        db.insert(tableName, null, values);

    }



    public ArrayList<TopScore> getAllRecordsFromTable(String tableName){

        ArrayList<TopScore> allRecords = new ArrayList<>();
        String theQuery;
        int tableSize;

        tableSize = getTableSize(tableName);
        if (tableSize == 0 )
            return null;

        theQuery= "SELECT * FROM " + tableName + " ORDER BY " + DB_PLAYER_SCORE + " DESC ;";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(theQuery,null);
        c.moveToFirst();

        do {
            TopScore anyRecord = new TopScore(c.getInt(DB_PLAYER_ID_INDEX), c.getString(DB_PLAYER_NAME_INDEX)
                    ,c.getInt(DB_PLAYER_SCORE_INDEX),c.getDouble(DB_PLAYER_LAT_INDEX),c.getDouble(DB_PLAYER_LONG_INDEX));
            allRecords.add(anyRecord);
            Log.d("id",""+ c.getInt(DB_PLAYER_ID_INDEX));
        }while (c.moveToNext());

        return allRecords;

    }

    public void deleteWorstRecord(String tableName) {
        SQLiteDatabase db = getWritableDatabase();

        String theQuery = "SELECT * FROM " + tableName + " ORDER BY " + DB_PLAYER_SCORE + " ASC;";

        Cursor c = db.rawQuery(theQuery, null);
        c.moveToFirst();
        int id = c.getInt(DB_PLAYER_ID_INDEX);

        theQuery = "DELETE FROM " + tableName + " WHERE " + DB_PLAYER_ID + " = " + id + " ;";
        db.execSQL(theQuery);
    }
}
