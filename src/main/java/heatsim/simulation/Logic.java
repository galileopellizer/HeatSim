package heatsim.simulation;

import heatsim.settings.Settings;

import java.util.Random;

public class Logic {

    public Grid grid;
    Random rand = new Random(1);

    //n - grid size
    public Logic(int width, int height) {
        grid = new Grid(width, height);
    }

    public void heatRandomPoints(int numberOfPoints) {

        for(int i = 0; i < numberOfPoints; i++) {
            Cell randomCell = getUntouchedRandomCell();

            randomCell.setTemperature(Settings.HEAT_RETENTION_THRESHOLD);
        }
    }

    private Cell getUntouchedRandomCell() {
        Cell randomCell;
        do {
            randomCell = grid.getRandomCellWithinBorder(rand);
        }while(randomCell.getTemperature() > 0);
        return randomCell;
    }


}
