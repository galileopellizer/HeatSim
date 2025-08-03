package heatsim.simulation;

import java.util.concurrent.atomic.AtomicLong;

public abstract class AbstractGrid {
    protected int width;
    protected int height;
    public static AtomicLong totalCalculations = new AtomicLong(0);
    protected double maxTempChange = -1;

    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public abstract void recalculateGrid();
    public abstract boolean isStable();
    public double getMaxTemperatureChange() { return maxTempChange; }


}