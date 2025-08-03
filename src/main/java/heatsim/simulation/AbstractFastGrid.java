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
    public double getTemperature(int idx) { return this.temperatures[idx]; }

    public void setTemperature(int x, int y, double temperature) {
        temperatures[width * y + x] = temperature;
    }
    public void setTemperature(int idx, double temperature) { temperatures[idx] = temperature; }

    public boolean isInsideBorderBounds(int idx) {
        return !(idx % width == 0 || idx % width > width-2 || idx <= width || idx >= (width*height) - width);
    }

    public double recalculateTemperaturesAndGetMaxTempChange(int x, double localMaxChange) {
        int rowOffset = x * width;
        for (int y = Settings.BORDER_SIZE; y < height - Settings.BORDER_SIZE; y++) {
            if (temperatures[rowOffset + y] == Settings.HEAT_RETENTION_THRESHOLD) continue;
            double totalSum = getNeighboursSum(rowOffset, y);
            if (totalSum == 0) continue;
            totalSum /= 4;
            temperatures[rowOffset + y] = totalSum;

            double tempChange = Math.abs(totalSum - previousTemperatures[rowOffset + y]);
            if (tempChange > localMaxChange) {
                localMaxChange = tempChange;
            }
           // totalCalculations.incrementAndGet();
        }
        return localMaxChange;
    }
}
