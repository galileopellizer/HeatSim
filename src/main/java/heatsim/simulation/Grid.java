package heatsim.simulation;

import heatsim.settings.Settings;
import java.util.Random;

public class Grid {
    Cell[][] grid;
    int width;
    int height;
    double maxTempChange = 100;
    public static int totalCalculations = 0;


    public Grid(int width, int height) {
        this.width = width;
        this.height = height;
        grid = new Cell[width][height];
        for (int x = 0; x < height; x++) {
            for (int y = 0; y < width; y++) {
                grid[x][y] = new Cell(x, y, 0);
            }
        }
    }
    public Grid(Grid other) {
        this.width = other.width;
        this.height = other.height;
        this.maxTempChange = other.maxTempChange;
        this.grid = new Cell[width][height];

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
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

        for (int i = 0; i < this.height; i++) {
            for (int j = 0; j < this.width; j++) {

                Cell cellCopy = gridCopy.getCellWithinBorder(i, j);
                if(!shouldCalculateCell(cellCopy)) continue;

                double tempBeforeCalculation = cellCopy.getTemperature();

                Cell[] neighbors = gridCopy.getNeighbors(cellCopy);
                double totalTemperature = sumTemperature(neighbors);
                totalTemperature /= neighbors.length;

                if(totalTemperature == 0) continue; // Needs testing
                totalCalculations++;

                Cell cell = this.getCellWithinBorder(cellCopy.getX(), cellCopy.getY());
                cell.setTemperature(totalTemperature);


                if(tempBeforeCalculation == cell.getTemperature()) continue;

                double change = Math.abs(tempBeforeCalculation - cell.getTemperature());

                if(change < this.maxTempChange) {this.maxTempChange = change;}
            }
        }
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
}
