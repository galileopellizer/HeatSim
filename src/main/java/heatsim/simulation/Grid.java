package heatsim.simulation;

public abstract class Grid {
    protected int width;
    protected int height;
    public static long totalCalculations = 0;
    protected double maxTempChange = -1;

    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public abstract void recalculateGrid();
    public abstract boolean isStable();


}