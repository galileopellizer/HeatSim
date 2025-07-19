package heatsim.simulation;

import heatsim.settings.Settings;

import java.util.Random;

public class Logic {

    private VisualGrid visualGrid;
    private FastGrid fastGrid;
    Random rand = new Random(1);

    public Grid getGrid() {
        if(Settings.GRAPHICS_ENABLED) return this.visualGrid;
        return this.fastGrid;
    }

    //n - grid size
    public Logic(int width, int height) {
        if(Settings.GRAPHICS_ENABLED) visualGrid = new VisualGrid(width, height);
        else fastGrid = new FastGrid(width, height);

    }

    public void heatRandomPoints(int numberOfPoints) {
        if(Settings.GRAPHICS_ENABLED) {
            for(int i = 0; i < numberOfPoints; i++) {
                Cell randomCell = getUntouchedRandomCell();

                randomCell.setTemperature(Settings.HEAT_RETENTION_THRESHOLD);
            }
        }else {
            fastGrid.heatRandomPoints(numberOfPoints, rand);
        }

    }

    private Cell getUntouchedRandomCell() {
        Cell randomCell;
        do {
            randomCell = visualGrid.getRandomCellWithinBorder(rand);
        }while(randomCell.getTemperature() > 0);
        return randomCell;
    }


}
