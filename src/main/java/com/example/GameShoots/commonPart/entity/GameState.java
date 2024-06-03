package com.example.GameShoots.commonPart.entity;

import com.google.gson.annotations.Expose;

public class GameState {
    @Expose
    private final Target[] targets;
    @Expose
    private final Player[] players;
    @Expose
    private final double fieldWidth;
    @Expose
    private final double fieldHeight;
    @Expose
    private String winner;
    @Expose
    private Boolean isFinished;
    @Expose
    private Boolean isPaused;

    public GameState(double fieldWidth, double fieldHeight, Target[] targets, int playerCount) {
        this.fieldWidth = fieldWidth;
        this.fieldHeight = fieldHeight;
        players = new Player[playerCount];
        this.targets = targets;
        this.isFinished = false;
        winner = null;
        isPaused = false;
    }

    public void setIsFinished(Boolean value) {
        isFinished = value;
    }
    public Boolean getIsFinished() {
        return isFinished;
    }
    public void setWinner(Player winner) {
        this.winner = winner.getNickname();
        winner.incrementRank();
    }
    public String getWinner() {
        return winner;
    }
    public Target[] getTargets() {
        return targets;
    }

    public Player[] getPlayers() {
        return players;
    }

    public double getFieldWidth() {
        return fieldWidth;
    }

    public double getFieldHeight() {
        return fieldHeight;
    }
    public Boolean getIsPaused() {
        return isPaused;
    }
    public void setIsPaused(Boolean isPaused) {
        this.isPaused = isPaused;
    }
}
