package heatsim;

import heatsim.simulation.Cell;
import heatsim.simulation.Grid;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
class GridTest {

    @Test
    void getCell() {
        Grid grid = new Grid(3, 3);
        Cell cell = grid.getCell(2, 2);
        assertNotNull(cell);
    }

}