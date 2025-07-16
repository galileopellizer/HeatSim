package heatsim.ui;

import heatsim.settings.Settings;
import heatsim.simulation.Grid;
import heatsim.simulation.Logic;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.canvas.Canvas;
import javafx.stage.Stage;
import javafx.scene.canvas.GraphicsContext;


public class HeatSimulationApp extends Application {

    //randPoints -> 0 in CHECK_TEMP -> false (za najboljsi effect - ni nujno), nato lahko klikaš po posameznih celicah, ko jih segreješ do 100 ostanejo konstantno 100


    private static Logic logic;

    @Override
    public void start(Stage stage) {

        //stage.show();

        HeatSimulationVisualizer visualizer = new HeatSimulationVisualizer();
        Canvas canvas = visualizer.initializeUI(stage, logic, Settings.GRID_WIDTH, Settings.GRID_HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();


        //grafika parallel - animacija

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



        long startTime = System.currentTimeMillis();
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if(logic.recalculateAndCheckStability() && Settings.END_SIM_ON_TEMPERATURE_THRESHOLD_REACHED) {
                    long endTime = System.currentTimeMillis();
                    long elapsedTime = endTime - startTime;
                     System.out.println("Simulation ended");
                      System.out.println("Elapsed time: " + elapsedTime + "ms");
                    System.out.println("Total calculations: " + Grid.totalCalculations);
                    stop();
                }
                visualizer.drawGrid(gc, logic.grid);
            }
        };

        timer.start();
    }

    public void runHeadLessSimulation() {

        long startTime = System.currentTimeMillis();
        while(logic.recalculateAndCheckStability()) {

        }
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        System.out.println("Simulation ended");
        System.out.println("Elapsed time: " + elapsedTime + "ms");
        System.out.println("Total calculations: " + Grid.totalCalculations);
    }

    public static void main(String[] args) {

        HeatSimulationApp app = new HeatSimulationApp();
        logic = new Logic(Settings.GRID_WIDTH, Settings.GRID_HEIGHT);
        logic.heatRandomPoint(Settings.RANDOM_POINTS_NUM);
        System.out.println("Simulation started");


        if(Settings.GRAPHICS_ENABLED) {
            launch(args);
        }else {
            app.runHeadLessSimulation();
        }
    }
}