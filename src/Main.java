import mpi.MPI;

import java.util.Arrays;
import java.util.Random;

public class Main {

    static final int NUM_POINTS_TO_HEAT = 500;
    static final int HEIGHT = 5000;
    static final int WIDTH = 5000;
    static final double EPSILON = 0.25;

    public static void main(String[] args) {
        float startTime = System.nanoTime();
        MPI.Init(args);

        double[] temperatures = new double[HEIGHT * WIDTH];
        int numPoints = NUM_POINTS_TO_HEAT;
        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();

        int root = 0;

        if(rank == root) {
            Random rndm = new Random();

            do {
                int idx = rndm.nextInt(temperatures.length);
                if(temperatures[idx] == 0 && !touchingBorder(idx)) {
                    temperatures[idx] = 100;
                    numPoints--;
                }
            }while(numPoints != 0);

        }

        double[] maxDifference = {-1};

        while (maxDifference[0] > EPSILON || maxDifference[0] == -1) {
            double[] t = recalculate(root, temperatures, rank, size);
            maxDifference[0] = getMaxDifference(t, temperatures);

            MPI.COMM_WORLD.Bcast(maxDifference, 0, 1, MPI.DOUBLE, root);

            if(rank == root) {
                //System.out.println("Max difference: " + getMaxDifference(t, temperatures));
                temperatures = t;
            }
        }


       // double[] t1 = recalculate(root, temperatures, rank, size);





        MPI.Finalize();
        float endTime = System.nanoTime();

        if(rank == root) {
            System.out.println("Max difference: " + maxDifference[0]);
            System.out.println("Total time: " + ((endTime - startTime) / 1000000) + " ms");
        }

    }

    public static double getMaxDifference(double[] arr1, double[] arr2) {
        double maxDiff = 0;
        for(int i = 0; i < arr1.length; i++) {
            double diff = Math.abs(arr1[i] - arr2[i]);
            if(diff > maxDiff) {
                maxDiff = diff;
            }
        }
        return maxDiff;
    }

    public static double[] recalculate(int root, double[] temperatures, int rank, int size) {
        MPI.COMM_WORLD.Bcast(temperatures, 0, temperatures.length, MPI.DOUBLE, root);
        //System.out.println("Rank: "+rank+" Temp: "+temperatures[0]);
        int chunkSize = temperatures.length / size;
        int start = rank * chunkSize;
        int end = start + chunkSize;
        if(rank == size-1) {
            end = temperatures.length - 1;
        }
        if(rank != 0) {
            start += 1;
        }
       // System.out.println("Rank: " + rank + ", Size: " + size + ", Start: " + start + ", End: " + end);

        double[] local = new double[HEIGHT * WIDTH];
        //Arrays.fill(local, 0);

        for(int i = start; i <= end; i++) {
            if(touchingBorder(i)) continue;
            if(temperatures[i] == 100) {
                local[i] = 100;
                continue;
            };
            double[] neighbours = getNeighbours(temperatures, i);
            double sum = 0;
            for(double neighbour : neighbours) {
                sum += neighbour;
            }
            sum /= 4;
            local[i] = sum;
        }

        double[] result = new double[HEIGHT * WIDTH];
        MPI.COMM_WORLD.Reduce(local, 0, result, 0, result.length, MPI.DOUBLE, MPI.SUM, root);
//        if(rank == root) {
//            printGrid(result);
//        }
        return result;
    }

    public static boolean touchingBorder(int idx) {
        return (idx % WIDTH == 0 || idx % WIDTH > WIDTH-2 || idx <= WIDTH || idx >= (WIDTH*HEIGHT) - WIDTH);
    }

    public static double[] getNeighbours(double[] temps, int idx) {
        double[] neighbours = new double[4];
        neighbours[0] = temps[idx-1];
        neighbours[1] = temps[idx+1];
        neighbours[2] = temps[idx-WIDTH];
        neighbours[3] = temps[idx+WIDTH];
        return neighbours;
    }

    public static void printGrid(double[] result) {
        for(int i = 0; i < result.length; i++) {
            System.out.print(result[i] + (((i+1) % WIDTH == 0) ? "\n" : " | "));
        }
    }
}