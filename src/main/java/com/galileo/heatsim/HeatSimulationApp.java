package com.galileo.heatsim;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.scene.canvas.GraphicsContext;


public class HeatSimulationApp extends Application {

    //randPoints -> 0 in CHECK_TEMP -> false (za najboljsi effect - ni nujno), nato lahko klikaš po posameznih celicah, ko jih segreješ do 100 ostanejo konstantno 100

    private final int GRID_WIDTH = 50;
    private final int GRID_HEIGHT = 50;
    private final int RANDOM_POINTS_NUM = 50;
    private final boolean GRAPHICS_ENABLED = true;
    private final boolean CHECK_TEMP = false; // true  - program bo prenehal simulacijo ko ne bo spremembe vecje od 0.25C
    public static final double TEMPERATURE_CHANGE_THRESHOLD = 0.025;

    private static HeatSimLogic logic;

    @Override
    public void start(Stage stage) {

        //stage.show();

        HeatSimulationVisualizer visualizer = new HeatSimulationVisualizer();
        Canvas canvas = visualizer.initializeUI(stage, logic, GRID_WIDTH, GRID_HEIGHT);
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
                if(logic.calculateGrid() && CHECK_TEMP) {
                    long endTime = System.currentTimeMillis();
                    long elapsedTime = endTime - startTime;
                     System.out.println("Simulation ended");
                      System.out.println("Elapsed time: " + elapsedTime + "ms");
                    stop();
                }
                visualizer.drawGrid(gc, logic.grid);
            }
        };

        timer.start();




/*
        // grafika sekvencno - no animation :(
        System.out.println("Simulation started");
        long startTime = System.currentTimeMillis();
        while(logic.calculateGrid()) {
            visualizer.drawGrid(gc, logic.getGrid());
        }
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        System.out.println("Simulation ended");
        System.out.println("Elapsed time: " + elapsedTime + "ms");

*/

    }

    public void runHeadLessSimulation() {

        long startTime = System.currentTimeMillis();
        while(logic.calculateGrid()) {

        }
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        System.out.println("Simulation ended");
        System.out.println("Elapsed time: " + elapsedTime + "ms");
    }

    public static void main(String[] args) {

        HeatSimulationApp app = new HeatSimulationApp();
        logic = new HeatSimLogic(app.GRID_WIDTH, app.GRID_HEIGHT);
        logic.heatRandomPoint(app.RANDOM_POINTS_NUM);
        System.out.println("Simulation started");

        if(app.GRAPHICS_ENABLED) {
            launch(args);
        }else {
            app.runHeadLessSimulation();
        }
    }
}