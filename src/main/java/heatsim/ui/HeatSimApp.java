package heatsim.ui;

import heatsim.settings.Mode;
import heatsim.settings.Settings;
import heatsim.simulation.AbstractGrid;
import heatsim.simulation.VisualGrid;
import heatsim.simulation.Logic;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.Set;


public class HeatSimApp extends Application {

    private static Logic logic;
    private static long startTime;

    @Override
    public void start(Stage stage) {

        Visualizer visualizer = new Visualizer(stage, logic);
        visualizer.initializeUI();


        //testing
/*
        canvas.setOnKeyPressed((KeyEvent event) -> {
            long startTime = System.currentTimeMillis();

            visualizer.drawGrid(gc, logic.grid);
            long endTime = System.currentTimeMillis();
            long elapsedTime = endTime - startTime;

        });

        canvas.setFocusTraversable(true);
*/


        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                visualizer.drawGrid();

                logic.getGrid().recalculateGrid();

                if (checkEndConditionAndStop()) stop();

            }
        };

        timer.start();
    }

    static int runs = 3;
    static int[] runtimes = new int[runs];
    //static long[] totalCalculations = new long[runs];
    static int c = 0;

    private boolean checkEndConditionAndStop() {
        if (!(Settings.END_SIM_ON_TEMPERATURE_THRESHOLD_REACHED && logic.getGrid().isStable())) return false;
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        // System.out.println("Simulation ended");
        runtimes[c] = (int) elapsedTime;
        //totalCalculations[c] = AbstractGrid.totalCalculations.get();
        c++;
        //AbstractGrid.totalCalculations.set(0);
         System.out.println("Elapsed time: " + elapsedTime + "ms");

        //System.out.println("Total calculations: " + AbstractGrid.totalCalculations);
        return true;
    }

    public void runHeadLessSimulation() {
        while (!checkEndConditionAndStop()) {
            logic.getGrid().recalculateGrid();
        }
        // System.out.println(logic.getGrid().getMaxTemperatureChange());
    }

        public static void main(String[] args){

        HeatSimApp app = new HeatSimApp();
        logic = new Logic(Settings.GRID_WIDTH, Settings.GRID_HEIGHT);
        logic.heatRandomPoints(Settings.RANDOM_POINTS_NUM);

        System.out.println("Simulation started");
        startTime = System.currentTimeMillis();

        if (Settings.MODE == Mode.VISUAL) {
            launch(args);
        } else {
            app.runHeadLessSimulation();
        }
        System.exit(0);
    }
//    public static void main(String[] args) {
//
//        System.out.println("grid_size,run1,run2,run3");
//        for (int j = 0; j < 41; j++) {
//            Settings.GRID_WIDTH += 10;
//            Settings.GRID_HEIGHT += 10;
//
//            for (int i = 0; i < runs; i++) {
//                //Settings.RANDOM_SEED = i + 1;
//                HeatSimApp app = new HeatSimApp();
//                logic = new Logic(Settings.GRID_WIDTH, Settings.GRID_HEIGHT);
//                logic.heatRandomPoints(Settings.RANDOM_POINTS_NUM);
//
//                //System.out.println("Simulation started (run " + (i+1) + ")");
//                startTime = System.currentTimeMillis();
//
//                app.runHeadLessSimulation();
//            }
//            c = 0;
//            System.out.print(Settings.GRID_WIDTH + ",");
//            for (int i = 0; i < runtimes.length; i++) {
//                System.out.print(runtimes[i] + ((i == runtimes.length - 1) ? "" : ","));
//            }
//            System.out.println();
//        }
//        System.exit(0);
//    }


//    public static void main(String[] args) {
//        System.out.println("heated_points,run1,run2,run3");
//        for (int j = 0; j < 50; j++) {
//            Settings.RANDOM_POINTS_NUM += 100;
//            for (int i = 0; i < runs; i++) {
//                Settings.RANDOM_SEED = i + 1;
//                HeatSimApp app = new HeatSimApp();
//                logic = new Logic(Settings.GRID_WIDTH, Settings.GRID_HEIGHT);
//                logic.heatRandomPoints(Settings.RANDOM_POINTS_NUM);
//
//                //System.out.println("Simulation started (run " + (i+1) + ")");
//                startTime = System.currentTimeMillis();
//
//                app.runHeadLessSimulation();
//
//                // Optionally: collect results here
//                // e.g., store elapsed time, max temp change, etc.
//            }
//            c = 0;
////        System.out.println("Heated points: "+Settings.RANDOM_POINTS_NUM);
////        System.out.print("Runtimes: ");
////        for(int i = 0; i < runtimes.length; i++) {
////            System.out.print(runtimes[i]+"ms"+((i == runtimes.length-1) ? "" : ", "));
////        }
//
//            System.out.print(Settings.RANDOM_POINTS_NUM + ",");
//            for (int i = 0; i < runtimes.length; i++) {
//                System.out.print(runtimes[i] + ((i == runtimes.length - 1) ? "" : ","));
//            }
//            System.out.println();
////        System.out.print("\nTotal calculations: ");
////        for (int i = 1; i < totalCalculations.length; i++) {
////            System.out.print(totalCalculations[i] + (i == totalCalculations.length-1 ? "" : ", "));
////        }
//        }
//    }
}
//
//    System.exit(0);
//}
//}