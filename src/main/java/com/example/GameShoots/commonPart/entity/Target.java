package com.example.GameShoots.commonPart.entity;

import com.google.gson.annotations.Expose;

public class Target {
    @Expose
    private double radius;
    @Expose
    private double x;
    @Expose
    private double y;
    private double maxY;
    private double minY;
    private double speed;
    private boolean movingUp = true;
    private int scoreIncrement;

    public int getScoreIncrement() {
        return scoreIncrement;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getRadius() {
        return radius;
    }
    public void step() {
        // движение мишени вверх вниз по прямой
        if (movingUp) {
            y += speed;
            if (y > maxY) {
                movingUp = false;
            }
        } else {
            y -= speed;
            if (y < minY) {
                movingUp = true;
            }
        }
    }
    public boolean isHit(Bullet bullet) {
        // проверка на попадание пули в мишень
        double b_x = bullet.getX();
        double b_y = bullet.getY();
        double b_r = bullet.getRadius();

        double dist = Math.sqrt(Math.pow(b_x - x, 2) + Math.pow(b_y - y, 2));

        return !(dist > b_r + radius || dist < Math.abs(b_r - radius));
    }

    public Target(double radius, double x, double y, double maxY, double minY, double speed, int scoreIncrement) {
        // конструктор инит
        this.radius = radius;
        this.x = x;
        this.y = y;
        this.maxY = maxY;
        this.minY = minY;
        this.speed = speed;
        this.scoreIncrement = scoreIncrement;
    }
}
