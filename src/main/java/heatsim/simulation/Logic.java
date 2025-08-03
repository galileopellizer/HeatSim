package heatsim.simulation;

import heatsim.settings.Mode;
import heatsim.settings.Settings;
import org.controlsfx.control.PropertySheet;

import java.util.Random;

public class Logic {

    private VisualGrid visualGrid;
    private AbstractFastGrid grid;
    Random rand = new Random(Settings.RANDOM_SEED);

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
        do{
            int idx = rand.nextInt(grid.temperatures.length);
            if(grid.temperatures[idx] == 0 && grid.isInsideBorderBounds(idx)) {
                grid.setTemperature(idx, Settings.HEAT_RETENTION_THRESHOLD);
                numOfPoints--;
            }
        }while(numOfPoints != 0);
    }

    private Cell getUntouchedRandomCell() {
        Cell randomCell;
        do {
            randomCell = visualGrid.getRandomCellWithinBorder(rand);
        }while(randomCell.getTemperature() > 0);
        return randomCell;
    }


}
