package com.galileo.heatsim;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.canvas.Canvas;
import javafx.stage.Stage;
import javafx.scene.canvas.GraphicsContext;


public class HeatSimulationApp extends Application {

    //randPoints -> 0 in CHECK_TEMP -> false (za najboljsi effect - ni nujno), nato lahko klikaš po posameznih celicah, ko jih segreješ do 100 ostanejo konstantno 100

    private final int w = 5;
    private final int h = 5;
    private final int RANDOM_POINTS_NUM = 0;
    private final boolean GRAPHICS_ENABLED = true;
    private final boolean CHECK_TEMP = false; // true  - program bo prenehal simulacijo ko ne bo spremembe vecje od 0.25C

    private HeatSimLogic logic;

    @Override
    public void start(Stage stage) {

        logic = new HeatSimLogic(w, h);
        logic.heatRandomPoint(RANDOM_POINTS_NUM);

        //stage.show();

        HeatSimulationVisualizer visualizer = new HeatSimulationVisualizer();
        Canvas canvas = visualizer.initializeUI(stage, logic, w, h);
        GraphicsContext gc = canvas.getGraphicsContext2D();


        //grafika parallel - animacija

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

    public void runHeadLessSimulation() {
        this.logic = new HeatSimLogic(this.w, this.h);
        this.logic.heatRandomPoint(this.RANDOM_POINTS_NUM);
        long startTime = System.currentTimeMillis();
        while(this.logic.calculateGrid()) {

        }
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        System.out.println("Simulation ended");
        System.out.println("Elapsed time: " + elapsedTime + "ms");
    }

    public static void main(String[] args) {

        HeatSimulationApp app = new HeatSimulationApp();
        System.out.println("Simulation started");

        if(app.GRAPHICS_ENABLED) {
            launch(args);
        }else {
            app.runHeadLessSimulation();
        }
    }
}