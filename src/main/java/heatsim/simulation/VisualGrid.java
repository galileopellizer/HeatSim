package heatsim.simulation;

import heatsim.settings.Settings;
import java.util.Random;

public class VisualGrid extends AbstractGrid {
    Cell[][] grid;

    public VisualGrid(int width, int height) {
        this.width = width;
        this.height = height;
        grid = new Cell[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                grid[x][y] = new Cell(new Position(x, y), 0);
            }
        }
    }
    public VisualGrid(VisualGrid other) {
        this.width = other.width;
        this.height = other.height;
        this.maxTempChange = other.maxTempChange;
        this.grid = new Cell[width][height];

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                this.grid[i][j] = new Cell(other.grid[i][j]);
            }
        }
    }

    public Cell getRandomCellWithinBorder(Random random) {
        int borderWidth = Settings.BORDER_SIZE;
        int x = random.nextInt(borderWidth, width-borderWidth);
        int y = random.nextInt(borderWidth, height-borderWidth);
        return grid[x][y];
    }

    public void heatUpCell(Cell cell) {
        cell.raiseTemperature(Settings.CLICK_HEATING_RATE);
    }

    public Cell[] getNeighbors(Cell cell) {
        Cell[] neighbors = new Cell[4];
        Position pos = cell.getPosition();

        neighbors[0] = getCell(pos.move(Direction.UP));
        neighbors[1] = getCell(pos.move(Direction.DOWN));
        neighbors[2] = getCell(pos.move(Direction.LEFT));
        neighbors[3] = getCell(pos.move(Direction.RIGHT));

        return neighbors;
    }

    public Cell getCell(Position pos) {

        if(pos.x() >= 0 && pos.x() <= width-1 && pos.y() >= 0 && pos.y() <= height-1) {
            return grid[pos.x()][pos.y()];
        }else {
            return null;
        }
    }

    public Cell getCellWithinBorder(int x, int y) {
        int offset = Settings.BORDER_SIZE;

        int upperXBoundary = width-(1 + offset);
        int upperYBoundary = height-(1 + offset);

        if(x >= offset && x <= upperXBoundary && y >= offset && y <= upperYBoundary) {
            return grid[x][y];
        }else {
            return null;
        }
    }

    public void recalculateGrid() {
        VisualGrid gridCopy = new VisualGrid(this);

        maxTempChange = 0;
        for (int i = Settings.BORDER_SIZE; i < this.width-Settings.BORDER_SIZE; i++) {
            for (int j = Settings.BORDER_SIZE; j < this.height-Settings.BORDER_SIZE; j++) {

                Cell cellCopy = gridCopy.getCell(new Position(i, j));
                if(!shouldCalculateCell(cellCopy)) continue;

                double totalTemperature = gridCopy.getNeighborsAverageTemp(cellCopy);

                if(totalTemperature == 0) continue; // Needs testing

                Cell cell = this.getCell(cellCopy.getPosition());
                cell.setTemperature(totalTemperature);
                updateMaxTempChange(getTempDifference(cellCopy.getTemperature(), cell.getTemperature()));

                totalCalculations.incrementAndGet();
            }
        }
    }

    private double getNeighborsAverageTemp(Cell cell) {
        Cell[] neighbors = this.getNeighbors(cell);
        return sumTemperature(neighbors) / neighbors.length;
    }

    private void updateMaxTempChange(double temp) {
        if(temp == 0) return;
        if(temp > this.maxTempChange) this.maxTempChange = temp;
    }

    private double getTempDifference(double temp1, double temp2) {
        return Math.abs(temp1 - temp2);
    }

    private double sumTemperature(Cell[] cells) {
        double sum = 0;
        for(Cell cell : cells) {
            sum += cell.getTemperature();
        }
        return sum;
    }

    private boolean shouldCalculateCell(Cell cell) {
        return !(cell == null || cell.isClicked());
    }

    public boolean isStable() {
        return maxTempChange <= Settings.TEMPERATURE_CHANGE_THRESHOLD && maxTempChange != -1;
    }
}
