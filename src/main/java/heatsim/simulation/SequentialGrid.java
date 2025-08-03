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
            this.maxTempChange = recalculateTemperaturesAndGetMaxTempChange(x, this.maxTempChange);
        }
    }





}
