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


    public Cell[][] getGrid() {
        return grid;
    }

    public Cell getRandomCellWithinBorder(Random random) {
        return grid[random.nextInt(1, width-1)][random.nextInt(1, height-1)];
    }

    public void heatUpCell(Cell cell) {
        cell.raiseTemperature(Settings.CLICK_HEATING_RATE);
    }

    public Cell[] getNeighbors(Cell cell) {
        Cell[] neighbors = new Cell[4];
        int cellX = cell.getX();
        int cellY = cell.getY();

        neighbors[0] = getCell(cellX-1, cellY, false);
        neighbors[1] = getCell(cellX+1, cellY, false);
        neighbors[2] = getCell(cellX, cellY-1, false);
        neighbors[3] = getCell(cellX, cellY+1, false);

        return neighbors;

    }

    public Cell getCell(int x, int y, boolean withinBorder) {

        int offset = withinBorder ? 1 : 0;

        int upperXBoundary = width-(1+offset);
        int upperYBoundary = height-(1+offset);

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

                Cell cellCopy = gridCopy.getCell(i, j, true);
                if (cellCopy == null || cellCopy.isClicked()) continue;
                totalCalculations++;
                double tempBeforeCalculation = cellCopy.getTemperature();

                Cell[] neighbors = gridCopy.getNeighbors(cellCopy);
                double totalTemperature = 0;
                for (Cell neighbor : neighbors) {
                    totalTemperature += neighbor.getTemperature();
                }
                totalTemperature /= neighbors.length;
                Cell cell = this.getCell(cellCopy.getX(), cellCopy.getY(), true);
                cell.setTemperature(totalTemperature);


                if(tempBeforeCalculation == cell.getTemperature()) continue;

                double change = Math.abs(tempBeforeCalculation - cell.getTemperature());

                if(change < this.maxTempChange) {this.maxTempChange = change;}
                //System.out.println("Maximum temperature change: " + this.MAXIMUM_TEMPERATURE_CHANGE);
            }
        }
    }
}
