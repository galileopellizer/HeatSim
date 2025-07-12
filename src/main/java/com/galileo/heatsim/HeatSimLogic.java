package com.galileo.heatsim;

import java.util.Random;

public class HeatSimLogic {

    double[][] grid;
    Random rand = new Random(1);
    int accessedX = -1, accessedY = -1;

    //n - grid size
    public HeatSimLogic(int w, int h) {
        System.out.println("HeatSimLogic");
        grid = new double[w][h];

    }



    public double[][] getGrid() {
        return grid;
    }


    public void heatRandomPoint(int n) {

        for(int i = 0; i < n; i++) {
            int x = rand.nextInt(grid.length);
            int y = rand.nextInt(grid[0].length);
            double old;
            do {
                old = grid[x][y];
                grid[x][y] = 100;
                x = rand.nextInt(grid.length);
                y = rand.nextInt(grid[0].length);
            }while(old != 0);
        }
    }

    public void heatUpCell(int y, int x) {
        grid[x][y] += 0.5;
        if(grid[x][y] > 100) {grid[x][y] = 100;}
    }

    public boolean calculateGrid() {

        double maxChange = 0;

        for(int i = 0; i < grid.length; i++) {

            for(int j = 0; j < grid[i].length; j++) {

                if(!(i == accessedX && j == accessedY)) {
                double old = grid[i][j];
                if(grid[i][j] != 100) {
                grid[i][j] = grid[i][(j == grid[i].length-1) ? j : j + 1] +
                                grid[(i == grid.length-1) ? i : i + 1][j] +
                                grid[i][(j == 0) ? j : j-1] + grid[(i == 0) ? i : i - 1][j];
                grid[i][j] /= 4;
                }
                double change = Math.abs(old - grid[i][j]);
                if(change > maxChange) {maxChange = change;}

                }
            }
        }



        //robi konstanta 0C
        /*
        for(int i = 0; i < grid.length; i++) {
            for(int j = 0; j < grid[i].length; j++) {
                if(i == 0 || j == 0 ||i == grid.length - 1 || j == grid[i].length - 1) {
                    grid[i][j] = 0;
                }
            }
        }

         */
        return !(maxChange <= 0.25);
    }
}
