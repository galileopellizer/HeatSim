import mpi.MPI;
public class Main {

    static final int NUM_POINTS_TO_HEAT = 2;
    static final int HEIGHT = 5;
    static final int WIDTH = 5;

    public static void main(String[] args) {
        MPI.Init(args);

        System.out.println("test");

        MPI.Finalize();
    }
}