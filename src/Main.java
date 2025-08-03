import mpi.MPI;
import java.util.Arrays;
import java.util.Random;

public class Main {
    static final int NUM_POINTS_TO_HEAT = 1000;
    static final double EPSILON = 0.25;

    public static void main(String[] args) {
        MPI.Init(args);
        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();
        int root = 0;
        int runs = 3;

        if (rank == root) {
            System.out.println("heated_points,run1,run2,run3");
        }

        for (int grid = 100; grid <= 5000; grid += 100) {
            int width = 1000;
            int height = 1000;
            int[] runtimes = new int[runs];

            for (int r = 0; r < runs; r++) {
                double[] temperatures = new double[width * height];
                //int numPoints = NUM_POINTS_TO_HEAT;

                if (rank == root) {
                    Random rndm = new Random(r + 1); // different seed for each run
                    int pointsLeft = grid;
                    do {
                        int idx = rndm.nextInt(temperatures.length);
                        if (temperatures[idx] == 0 && !touchingBorder(idx, width, height)) {
                            temperatures[idx] = 100;
                            pointsLeft--;
                        }
                    } while (pointsLeft != 0);
                }

                double[] maxDifference = {-1};
                long startTime = 0;
                if (rank == root) startTime = System.currentTimeMillis();

                while (maxDifference[0] > EPSILON || maxDifference[0] == -1) {
                    double[] t = recalculate(root, temperatures, rank, size, width, height);
                    maxDifference[0] = getMaxDifference(t, temperatures);
                    MPI.COMM_WORLD.Bcast(maxDifference, 0, 1, MPI.DOUBLE, root);
                    if (rank == root) {
                        temperatures = t;
                    }
                }

                long endTime = 0;
                if (rank == root) {
                    endTime = System.currentTimeMillis();
                    runtimes[r] = (int) (endTime - startTime);
                }
            }

            if (rank == root) {
                System.out.print(grid + ",");
                for (int i = 0; i < runtimes.length; i++) {
                    System.out.print(runtimes[i] + (i == runtimes.length - 1 ? "" : ","));
                }
                System.out.println();
            }
        }

        MPI.Finalize();
    }

    public static double getMaxDifference(double[] arr1, double[] arr2) {
        double maxDiff = 0;
        for (int i = 0; i < arr1.length; i++) {
            double diff = Math.abs(arr1[i] - arr2[i]);
            if (diff > maxDiff) {
                maxDiff = diff;
            }
        }
        return maxDiff;
    }

    public static double[] recalculate(int root, double[] temperatures, int rank, int size, int width, int height) {
        MPI.COMM_WORLD.Bcast(temperatures, 0, temperatures.length, MPI.DOUBLE, root);
        int chunkSize = temperatures.length / size;
        int start = rank * chunkSize;
        int end = (rank == size - 1) ? temperatures.length - 1 : start + chunkSize - 1;
        if (rank != 0) start += 1;

        double[] local = new double[width * height];
        for (int i = start; i <= end; i++) {
            if (touchingBorder(i, width, height)) continue;
            if (temperatures[i] == 100) {
                local[i] = 100;
                continue;
            }
            double[] neighbours = getNeighbours(temperatures, i, width);
            double sum = 0;
            for (double neighbour : neighbours) sum += neighbour;
            sum /= 4;
            local[i] = sum;
        }

        double[] result = new double[width * height];
        MPI.COMM_WORLD.Reduce(local, 0, result, 0, result.length, MPI.DOUBLE, MPI.SUM, root);
        return result;
    }

    public static boolean touchingBorder(int idx, int width, int height) {
        return (idx % width == 0 || idx % width > width - 2 || idx < width || idx >= (width * height) - width);
    }

    public static double[] getNeighbours(double[] temps, int idx, int width) {
        double[] neighbours = new double[4];
        neighbours[0] = temps[idx - 1];
        neighbours[1] = temps[idx + 1];
        neighbours[2] = temps[idx - width];
        neighbours[3] = temps[idx + width];
        return neighbours;
    }
}