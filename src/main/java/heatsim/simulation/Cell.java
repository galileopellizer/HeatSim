package heatsim.simulation;

import heatsim.settings.Settings;

public class Cell {
    private double temperature;


    private Position position;
    private boolean isClicked;

    private boolean dirty = true;


    public Cell(Position position, double temperature) {
        this.temperature = 0;
        this.position = position;
        this.isClicked = false;
    }

    public Cell(Cell other) {
        this.temperature = other.temperature;
        this.position = other.position;
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
        markDirty();
    }
    public void raiseTemperature(double amount) {
        if(temperatureAboveThreshold()) return;
        this.temperature += amount;
        markDirty();
    }

    private boolean temperatureAboveThreshold() {
        return this.temperature >= Settings.HEAT_RETENTION_THRESHOLD;
    }

    public Position getPosition() {
        return position;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void markDirty() {
        this.dirty = true;
    }

    public void clearDirty() {
        this.dirty = false;
    }

    public int getX() {
        return this.position.x();
    }
    public int getY() {
        return this.position.y();
    }
}
