package heatsim;

import heatsim.simulation.Cell;
import heatsim.simulation.Grid;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
class GridTest {

    @Test
    void getCell() {
        Grid grid = new Grid(3, 3);
        Cell cell = grid.getCell(2, 2, false);
        assertNotNull(cell);
    }

    @Test
    void calculateGrid() {
        Grid grid = new Grid(5, 5);
        grid.getCell(2, 2, true).setTemperature(100);
        grid.recalculateGrid();
        assertEquals(0, grid.getCell(4, 4, false).getTemperature());
        assertEquals(100, grid.getCell(2, 2, false).getTemperature());
        assertEquals(0.25, grid.getCell(2, 3, true).getTemperature());
        assertEquals(0.25, grid.getCell(3, 2, true).getTemperature());
        assertEquals(0.25, grid.getCell(1, 2, true).getTemperature());
        assertEquals(0.25, grid.getCell(2, 1, true).getTemperature());

    }


}