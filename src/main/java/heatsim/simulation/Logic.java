package heatsim.simulation;

import heatsim.settings.Mode;
import heatsim.settings.Settings;

import java.util.Random;

public class Logic {

    private VisualGrid visualGrid;
    private AbstractFastGrid grid;
    Random rand = new Random(1);

    public AbstractGrid getGrid() {
        if(Settings.MODE == Mode.VISUAL) return this.visualGrid;
        return this.grid;
    }

    //n - grid size
    public Logic(int width, int height) {
        if(Settings.MODE == Mode.VISUAL) visualGrid = new VisualGrid(width, height);
        else if(Settings.MODE == Mode.SEQUENTIAL) grid = new SequentialGrid(width, height);
        else if(Settings.MODE == Mode.PARALLEL) grid = new ParallelGrid(width, height);
    }

    public void heatRandomPoints(int numberOfPoints) {
        if(Settings.MODE == Mode.VISUAL) {
            for(int i = 0; i < numberOfPoints; i++) {
                Cell randomCell = getUntouchedRandomCell();

                randomCell.setTemperature(Settings.HEAT_RETENTION_THRESHOLD);
            }
        }else {
            heatFastGridRandomPoints(numberOfPoints, rand);
        }

    }

    private void heatFastGridRandomPoints(int numOfPoints, Random rand) {
        for(int i = 0; i < numOfPoints; i++) {
            int x;
            int y;
            do {
                x = rand.nextInt(Settings.BORDER_SIZE, grid.getHeight() - Settings.BORDER_SIZE);
                y = rand.nextInt(Settings.BORDER_SIZE, grid.getWidth() - Settings.BORDER_SIZE);
            }while(grid.getTemperature(x, y) != 0);
            grid.setTemperature(x, y, Settings.HEAT_RETENTION_THRESHOLD);
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
