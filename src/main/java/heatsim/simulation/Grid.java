package heatsim.simulation;

import heatsim.settings.Settings;
import java.util.Random;

public class Grid {
    Cell[][] grid;
    int width;
    int height;
    double maxTempChange = 0;
    public static int totalCalculations = 0;


    public Grid(int width, int height) {
        this.width = width;
        this.height = height;
        grid = new Cell[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                grid[x][y] = new Cell(x, y, 0);
            }
        }
    }
    public Grid(Grid other) {
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
        int borderWidth = Settings.BORDER_WIDTH;
        int x = random.nextInt(borderWidth, width-borderWidth);
        int y = random.nextInt(borderWidth, height-borderWidth);
        return grid[x][y];
    }

    public void heatUpCell(Cell cell) {
        cell.raiseTemperature(Settings.CLICK_HEATING_RATE);
    }

    public Cell[] getNeighbors(Cell cell) {
        Cell[] neighbors = new Cell[4];
        int cellX = cell.getX();
        int cellY = cell.getY();

        neighbors[0] = getCell(cellX-1, cellY);
        neighbors[1] = getCell(cellX+1, cellY);
        neighbors[2] = getCell(cellX, cellY-1);
        neighbors[3] = getCell(cellX, cellY+1);


        return neighbors;

    }

    public Cell getCell(int x, int y) {
        if(x >= 0 && x <= width-1 && y >= 0 && y <= height-1) {
            return grid[x][y];
        }else {
            return null;
        }
    }

    public Cell getCellWithinBorder(int x, int y) {
        int offset = Settings.BORDER_WIDTH;

        int upperXBoundary = width-(1 + offset);
        int upperYBoundary = height-(1 + offset);

        if(x >= offset && x <= upperXBoundary && y >= offset && y <= upperYBoundary) {
            return grid[x][y];
        }else {
            return null;
        }
    }

    public void recalculateGrid() {
        Grid gridCopy = new Grid(this);

        maxTempChange = 0;
        for (int i = Settings.BORDER_WIDTH; i < this.width-Settings.BORDER_WIDTH; i++) {
            for (int j = Settings.BORDER_WIDTH; j < this.height-Settings.BORDER_WIDTH; j++) {

                Cell cellCopy = gridCopy.getCell(i, j);
                if(!shouldCalculateCell(cellCopy)) continue;

                double totalTemperature = gridCopy.getNeighborsAverageTemp(cellCopy);

                if(totalTemperature == 0) continue; // Needs testing
                totalCalculations++;

                Cell cell = this.getCell(cellCopy.getX(), cellCopy.getY());
                cell.setTemperature(totalTemperature);


                updateMaxTempChange(getTempDifference(cellCopy.getTemperature(), cell.getTemperature()));
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
        return maxTempChange <= Settings.TEMPERATURE_CHANGE_THRESHOLD;
    }
}
