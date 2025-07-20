package heatsim.simulation;

import heatsim.settings.Settings;

import java.util.Arrays;
import java.util.Random;

public class SequentialGrid extends AbstractFastGrid {

    public SequentialGrid(int width, int height) {
        temperatures = new double[width * height];
        Arrays.fill(temperatures, 0);
        this.width = width;
        this.height = height;
        previousTemperatures = new double[width * height];
    }





    public void recalculateGrid() {
        System.arraycopy(temperatures, 0, previousTemperatures, 0, height*width);

        this.maxTempChange = 0;
        for(int x = Settings.BORDER_SIZE; x < this.width-Settings.BORDER_SIZE; x++) {
            int rowOffset = x * width;
            for(int y = Settings.BORDER_SIZE; y < this.height-Settings.BORDER_SIZE; y++) {
                if(temperatures[rowOffset + y] == Settings.HEAT_RETENTION_THRESHOLD) continue;
                double totalSum = getNeighboursSum(rowOffset, y);
                if(totalSum == 0) continue;
                totalSum /= 4;
                temperatures[rowOffset + y] = totalSum;

                double tempChange = Math.abs(totalSum-previousTemperatures[rowOffset + y]);

                if(tempChange > this.maxTempChange) {
                    this.maxTempChange = tempChange;
                }
                totalCalculations.incrementAndGet();
            }
        }
    }





}
