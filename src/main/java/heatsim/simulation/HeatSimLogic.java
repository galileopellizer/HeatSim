package heatsim.simulation;

import heatsim.settings.Settings;

import java.util.Random;

public class HeatSimLogic {

    public Grid grid;
    Random rand = new Random(1);

    //n - grid size
    public HeatSimLogic(int width, int height) {
        grid = new Grid(width, height);
    }

    public void heatRandomPoint(int numberOfPoints) {

        for(int i = 0; i < numberOfPoints; i++) {
            Cell randomCell;
            do {
                randomCell = grid.getRandomCellWithinBorder(rand);
            }while(randomCell.getTemperature() > 0);
            randomCell.setTemperature(100);
        }
    }


    public boolean calculateGrid() {

        grid.recalculateGrid();
        return grid.maxTempChange <= Settings.TEMPERATURE_CHANGE_THRESHOLD;

    }


}
