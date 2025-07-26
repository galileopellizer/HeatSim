package heatsim.ui;

import heatsim.settings.Mode;
import heatsim.settings.Settings;
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

    private boolean checkEndConditionAndStop() {
        if (!(Settings.END_SIM_ON_TEMPERATURE_THRESHOLD_REACHED && logic.getGrid().isStable())) return false;
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        System.out.println("Simulation ended");
        System.out.println("Elapsed time: " + elapsedTime + "ms");
        System.out.println("Total calculations: " + VisualGrid.totalCalculations);
        return true;
    }

    public void runHeadLessSimulation() {
        while (!checkEndConditionAndStop()) {
            logic.getGrid().recalculateGrid();
        }
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
}