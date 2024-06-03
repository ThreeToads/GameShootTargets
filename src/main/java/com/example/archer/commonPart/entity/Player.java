package com.example.archer.commonPart.entity;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

public class Player {
    @Expose
    private final int id;
    @Expose
    private final String nickname;
    @Expose
    private double x;
    @Expose
    private double y;
    @Expose
    private int score = 0;
    @Expose
    private int shotsCount = 0;
    @Expose
    private Boolean ready = false;
    @Expose
    private int placeInRanking = 0;
    @Expose
    private ArrayList<Bullet> bullets = new ArrayList<>();

    public Player(int id, double x, double y, String nickname, int placeInRanking) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.nickname = nickname;
        this.placeInRanking = placeInRanking;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getScore() {
        return score;
    }

    public int getShotsCount() {
        return shotsCount;
    }

    public void incrementShots() {
        shotsCount++;
    }

    public void incrementScore(int increment) {
        score += increment;
    }

    public ArrayList<Bullet> getBullets() {
        return bullets;
    }

    public void setBullets(ArrayList<Bullet> bullets) {
        this.bullets = bullets;
    }

    public void shoot() {
        this.bullets.add(new Bullet(this.x, this.y, 5, 5));
        //this.score--;
        incrementShots();
    }

    public Boolean isReady() {
        return ready;
    }

    public void setReady(Boolean ready) {
        this.ready = ready;
    }

    public String getNickname() {
        return nickname;
    }
    public void reset() {
        score = 0;
        shotsCount = 0;
    }

    public int getRank() {
        return placeInRanking;
    }
    public void incrementRank() {
        setRank(placeInRanking + 1);
    }
    public void setRank(int rank) {
        this.placeInRanking = rank;
    }

    public int getId() {
        return this.id;
    }
}
