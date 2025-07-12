package com.galileo.heatsim;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.canvas.Canvas;
import javafx.stage.Stage;
import javafx.scene.canvas.GraphicsContext;


public class HeatSimulationApp extends Application {

    //randPoints -> 0 in CHECK_TEMP -> false (za najboljsi effect - ni nujno), nato lahko klikaš po posameznih celicah, ko jih segreješ do 100 ostanejo konstantno 100

    private final int w = 50;
    private final int h = 50;
    private final int randPoints = 3;
    private final boolean GRAPHICS_ENABLED = true;
    private final boolean CHECK_TEMP = false; // true  - program bo prenehal simulacijo ko ne bo spremembe vecje od 0.25C
    private HeatSimLogic logic;

    @Override
    public void start(Stage stage) {


        logic = new HeatSimLogic(w, h);
        logic.heatRandomPoint(randPoints);

        stage.show();


        HeatSimulationVisualizer visualizer = new HeatSimulationVisualizer();
        Canvas canvas = visualizer.initializeUI(stage, logic, w, h);
        GraphicsContext gc = canvas.getGraphicsContext2D();





/*

    //testing
        canvas.setOnKeyPressed((KeyEvent event) -> {
            long startTime = System.currentTimeMillis();

            System.out.println(logic.calculateGrid());
            visualizer.drawGrid(gc, logic.getGrid());
            long endTime = System.currentTimeMillis();
            long elapsedTime = endTime - startTime;

        });

        canvas.setFocusTraversable(true);

 */




        //grafika parallel - animacija
        System.out.println("Simulation started");
        long startTime = System.currentTimeMillis();
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {

                if(!logic.calculateGrid() && CHECK_TEMP) {
                    long endTime = System.currentTimeMillis();
                    long elapsedTime = endTime - startTime;
                     System.out.println("Simulation ended");
                      System.out.println("Elapsed time: " + elapsedTime + "ms");
                    stop();
                }
                visualizer.drawGrid(gc, logic.getGrid());

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

    public static void main(String[] args) {

        HeatSimulationApp app = new HeatSimulationApp();


        if(app.GRAPHICS_ENABLED) {
            launch(args);
        }else {
            app.logic = new HeatSimLogic(app.w, app.h);
            long startTime = System.currentTimeMillis();
            while(app.logic.calculateGrid()) {

            }
            long endTime = System.currentTimeMillis();
            long elapsedTime = endTime - startTime;
            System.out.println("Simulation ended");
            System.out.println("Elapsed time: " + elapsedTime + "ms");
        }
    }
}