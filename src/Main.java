import mpi.MPI;

import java.util.Arrays;
import java.util.Random;

public class Main {

    static final int NUM_POINTS_TO_HEAT = 2;
    static final int HEIGHT = 10;
    static final int WIDTH = 10;

    public static void main(String[] args) {
        MPI.Init(args);

        int[] temperatures = new int[HEIGHT * WIDTH];
        int numPoints = NUM_POINTS_TO_HEAT;
        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();

        int root = 0;

        if(rank == root) {
            Random rndm = new Random();
            for(int i = 0; i < temperatures.length; i++) {
                temperatures[i] = 0;
            }

            do {
                int idx = rndm.nextInt(temperatures.length);
                if(temperatures[idx] == 0 && !touchingBorder(idx)) {
                    temperatures[idx] = 100;
                    numPoints--;
                }
            }while(numPoints != 0);



            for(int i = 0; i < temperatures.length; i++) {
                System.out.print(temperatures[i] + (((i+1) % WIDTH == 0) ? "\n" : " "));
            }

        }

        int[] t = recalculate(root, temperatures, rank, size);
        if(rank == root) {
            temperatures = t;
        }
        int[] t1 = recalculate(root, temperatures, rank, size);





        MPI.Finalize();
    }

    public static int[] recalculate(int root, int[] temperatures, int rank, int size) {
        MPI.COMM_WORLD.Bcast(temperatures, 0, temperatures.length, MPI.INT, root);
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
        System.out.println("Rank: " + rank + ", Size: " + size + ", Start: " + start + ", End: " + end);

        int[] local = new int[HEIGHT * WIDTH];
        //Arrays.fill(local, 0);

        for(int i = start; i <= end; i++) {
            if(touchingBorder(i)) continue;
            if(temperatures[i] == 100) {
                local[i] = 100;
                continue;
            };
            int[] neighbours = getNeighbours(temperatures, i);
            int sum = 0;
            for(int neighbour : neighbours) {
                sum += neighbour;
            }
            sum /= 4;
            local[i] = sum;
        }

        int[] result = new int[HEIGHT * WIDTH];
        MPI.COMM_WORLD.Reduce(local, 0, result, 0, result.length, MPI.INT, MPI.SUM, root);
        if(rank == root) {
            for(int i = 0; i < result.length; i++) {
                System.out.print(result[i] + (((i+1) % WIDTH == 0) ? "\n" : " "));
            }

        }
        return result;
    }

    public static boolean touchingBorder(int idx) {
        return (idx % WIDTH == 0 || idx % WIDTH > WIDTH-2 || idx <= WIDTH || idx >= (WIDTH*HEIGHT) - WIDTH);
    }

    public static int[] getNeighbours(int[] temps, int idx) {
        int[] neighbours = new int[4];
        neighbours[0] = temps[idx-1];
        neighbours[1] = temps[idx+1];
        neighbours[2] = temps[idx-WIDTH];
        neighbours[3] = temps[idx+WIDTH];
        return neighbours;
    }
}