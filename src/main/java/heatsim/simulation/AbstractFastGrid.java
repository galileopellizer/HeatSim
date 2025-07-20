package heatsim.simulation;

import heatsim.settings.Settings;

public abstract class AbstractFastGrid extends AbstractGrid {
    double[] temperatures, previousTemperatures;

    protected double getNeighboursSum(int rowOffset, int y) {
        double sum = 0;
        sum += previousTemperatures[rowOffset - width + y];
        sum += previousTemperatures[rowOffset + width + y];
        sum += previousTemperatures[rowOffset + y - 1];
        sum += previousTemperatures[rowOffset + y + 1];
        return sum;
    }
    public boolean isStable() {
        return this.maxTempChange <= Settings.TEMPERATURE_CHANGE_THRESHOLD && this.maxTempChange != -1;
    }

    public double getTemperature(int x, int y) {
        return temperatures[width * y + x];
    }

    public void setTemperature(int x, int y, double temperature) {
        temperatures[width * y + x] = temperature;
    }
}
