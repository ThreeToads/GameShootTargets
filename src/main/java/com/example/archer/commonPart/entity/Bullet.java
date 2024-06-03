package com.example.archer.commonPart.entity;

import com.google.gson.annotations.Expose;

public class Bullet {
    @Expose
    private double y;
    @Expose
    private double x;
    @Expose
    private final int radius;

    private final double speed;
    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getRadius() {
        return radius;
    }

    public void step() {
        x += speed;
    }

    public Bullet(double x, double y, int radius, double speed) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.speed = speed;
    }
}
