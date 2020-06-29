package com.example.dani_pc.bobo;

public class TopScore {
    private int id;
    private String name;
    private int score;
    private double latitude,longtitude;


    public TopScore(String name, int score, double latitude, double longtitude){

        this.name=name;
        this.score=score;
        this.latitude=latitude;
        this.longtitude=longtitude;
    }


    public TopScore(int id, String name, int score, double latitude, double longtitude){
        this.id=id;
        this.name=name;
        this.score=score;
        this.latitude=latitude;
        this.longtitude=longtitude;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongtitude() {
        return longtitude;
    }

}
