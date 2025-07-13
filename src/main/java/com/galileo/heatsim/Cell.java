package com.galileo.heatsim;

public class Cell {
    private double temperature;
    static double HEAT_RETENTION_THRESHOLD = 100;

    private final int x;
    private final int y;
    private boolean isClicked;



    public Cell(int x, int y, double temperature) {
        this.temperature = 0;
        this.x = x;
        this.y = y;
        this.isClicked = false;
    }

    public Cell(Cell other) {
        this.temperature = other.temperature;
        this.x = other.x;
        this.y = other.y;
        this.isClicked = other.isClicked;
    }



    public void setClicked(boolean clicked) {
        this.isClicked = clicked;
    }
    public boolean isClicked() {
        return isClicked;
    }

    public double getTemperature() {
        return temperature;
    }
    public void setTemperature(double temperature) {
        if(this.temperature >= HEAT_RETENTION_THRESHOLD) return;
        this.temperature = temperature;
    }
    public void raiseTemperature(double amount) {
        if(this.temperature >= HEAT_RETENTION_THRESHOLD) return;
        this.temperature += amount;
    }

    public int getX() {
        return this.x;
    }
    public int getY() {
        return this.y;
    }
}
