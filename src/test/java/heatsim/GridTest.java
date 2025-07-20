package heatsim;

import heatsim.settings.Settings;
import heatsim.simulation.Cell;
import heatsim.simulation.VisualGrid;
import heatsim.simulation.SequentialGrid;
import heatsim.simulation.Position;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class GridTest {

    @Test
    void getCell() {
        VisualGrid grid = new VisualGrid(3, 3);
        Cell cell = grid.getCell(new Position(2, 2));
        assertNotNull(cell);
    }
    @Test
    public void testGridAndVisualGridCalculationsAreEqual() {
        int width = 5000;
        int height = 5000;
        int numberOfPoints = 500;
        double initialTemp = Settings.HEAT_RETENTION_THRESHOLD;

        // Use the same seed for reproducibility
        Random rand = new Random(1);

        // Create both grids
        SequentialGrid sequentialGrid = new SequentialGrid(width, height);
        VisualGrid visualGrid = new VisualGrid(width, height);

        // Set the same random hot points in both grids
        for (int i = 0; i < numberOfPoints; i++) {
            // Find a random untouched cell in VisualGrid
            Cell randomCell;
            do {
                randomCell = visualGrid.getRandomCellWithinBorder(rand);
            } while (randomCell.getTemperature() > 0);

            // Set temperature in VisualGrid
            randomCell.setTemperature(initialTemp);

            // Set temperature in FastGrid at the same position
            Position pos = randomCell.getPosition();
            sequentialGrid.setTemperature(pos.x(), pos.y(), initialTemp);
        }

        // Run calculation step
        do {
            sequentialGrid.recalculateGrid();
            visualGrid.recalculateGrid();
        }while(!sequentialGrid.isStable() && !visualGrid.isStable());

        // Compare results
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                double fastTemp = sequentialGrid.getTemperature(i, j);
                double visualTemp = visualGrid.getCell(new Position(i, j)).getTemperature();
                assertEquals(fastTemp, visualTemp, 1e-6,
                        "Mismatch at (" + i + "," + j + ")");
            }
        }
    }

}