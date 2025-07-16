package heatsim.simulation;

import heatsim.settings.Settings;

public class Cell {
    private double temperature;


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
        if(temperatureAboveThreshold()) return;
        this.temperature = temperature;
    }
    public void raiseTemperature(double amount) {
        if(temperatureAboveThreshold()) return;
        this.temperature += amount;
    }

    private boolean temperatureAboveThreshold() {
        return this.temperature >= Settings.HEAT_RETENTION_THRESHOLD;
    }

    public int getX() {
        return this.x;
    }
    public int getY() {
        return this.y;
    }
}
