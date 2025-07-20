package heatsim.simulation;

import heatsim.settings.Settings;

import java.util.Random;
import java.util.Set;

public class FastGrid extends Grid {
    double[][] temperatures, previousTemperatures;

    public FastGrid(int rows, int cols) {
        temperatures = new double[rows][cols];
        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < cols; j++) {
                temperatures[i][j] = 0;
            }
        }
        this.height = rows;
        this.width = cols;
    }

    public double getTemperature(int row, int col) {
        return temperatures[row][col];
    }

    public void setTemperature(int row, int col, double temperature) {
        temperatures[row][col] = temperature;
    }

    public void heatRandomPoints(int numOfPoints, Random rand) {
        for(int i = 0; i < numOfPoints; i++) {
            int row;
            int col;
            do {
                row = rand.nextInt(Settings.BORDER_WIDTH, this.height - Settings.BORDER_WIDTH);
                col = rand.nextInt(Settings.BORDER_WIDTH, this.width - Settings.BORDER_WIDTH);
            }while(temperatures[row][col] != 0);
            temperatures[row][col] = 100;
        }
    }

    public void recalculateGrid() {
        previousTemperatures = new double[width][height];
        for (int i = 0; i < width; i++) {
            System.arraycopy(temperatures[i], 0, previousTemperatures[i], 0, height);
        }

        this.maxTempChange = 0;
        for(int i = Settings.BORDER_WIDTH; i < this.width-Settings.BORDER_WIDTH; i++) {
            for(int j = Settings.BORDER_WIDTH; j < this.height-Settings.BORDER_WIDTH; j++) {
                if(temperatures[i][j] == 100)continue;
                double amount = 0;
                amount += previousTemperatures[i-1][j];
                amount += previousTemperatures[i+1][j];
                amount += previousTemperatures[i][j-1];
                amount += previousTemperatures[i][j+1];
                if(amount == 0) continue;
                amount /= 4;
                temperatures[i][j] = amount;
                if(Math.abs(amount-previousTemperatures[i][j]) > this.maxTempChange) {
                    this.maxTempChange = Math.abs(amount-previousTemperatures[i][j]);
                }
                totalCalculations++;
            }
        }


    }


    public boolean isStable() {
        if(this.maxTempChange <= Settings.TEMPERATURE_CHANGE_THRESHOLD && this.maxTempChange != -1) {
            return true;
        }
        return false;
    }
}
