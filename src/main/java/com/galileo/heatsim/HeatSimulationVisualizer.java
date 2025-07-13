package com.galileo.heatsim;

import javafx.animation.AnimationTimer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class HeatSimulationVisualizer {

    private static int GRID_WIDTH;
    private static int GRID_HEIGHT;


    private static  double  CELL_SIZE;
    private static final int MARGIN_LEFT_LABELS = 15;
    private static final int MARGIN_BOTTOM_LABELS = 15;
    private static final int MIN_MARGIN = 50; // priporoceno 50, razdalja med labels

    private void drawLegend(ObservableList<Text> leftLabels, ObservableList<Text> bottomLabels) {
        int stepX = 1;
        int stepY = 1;

        int gridHeightPX = (GRID_HEIGHT * (int)CELL_SIZE);
        int gridWidthPX = (GRID_WIDTH * (int)CELL_SIZE);

        int labelMarginX = gridWidthPX / (GRID_WIDTH / stepX);
        int labelMarginY = gridHeightPX / (GRID_HEIGHT / stepY);

        stepY = (labelMarginY < MIN_MARGIN) ? 5 : stepY;

        //steps vrednosti 1, 5, 10
        while(labelMarginY < MIN_MARGIN) {


            if(stepY > GRID_HEIGHT) {
                stepY = GRID_HEIGHT;
                break;
            }
            labelMarginY = gridHeightPX / (GRID_HEIGHT / stepY);
            stepY += (labelMarginY < MIN_MARGIN) ? 5 : 0;

        }

        stepX = (labelMarginX < MIN_MARGIN) ? 5 : stepX;

        while(labelMarginX < MIN_MARGIN) {


            if(stepX > GRID_WIDTH) {
                stepX = GRID_WIDTH;
                break;
            }
            labelMarginX = gridWidthPX / (GRID_WIDTH / stepX);
            stepX += (labelMarginX < MIN_MARGIN) ? 5 : 0;

        }

        for (int i = stepY; i <= GRID_HEIGHT; i += stepY) {
            Text leftText = new Text(String.valueOf(i));
            leftText.setFill(Color.BLACK);
            leftText.setTranslateY(-((i) * CELL_SIZE) + (CELL_SIZE/2));
            leftText.setTranslateX(-MARGIN_LEFT_LABELS);
            leftLabels.add(leftText);
        }


        for (int i = stepX; i <= GRID_WIDTH; i += stepX) {
            Text bottomText = new Text(String.valueOf(i));
            bottomText.setFill(Color.BLACK);
            bottomText.setTranslateY(MARGIN_BOTTOM_LABELS);
            bottomText.setTranslateX(CELL_SIZE * i  - (CELL_SIZE / 2));
            bottomLabels.add(bottomText);
        }
    }

    public void drawGrid(GraphicsContext gc, double[][] grid) {
        for (int i = 0; i < GRID_WIDTH; i++) {
            for (int j = 0; j < GRID_HEIGHT; j++) {

                gc.setFill(getColor(grid[i][j])); // Začetna barva
                gc.fillRect(i * CELL_SIZE, j * CELL_SIZE, CELL_SIZE, CELL_SIZE);

                if(CELL_SIZE > 3) {
                    gc.setStroke(Color.BLACK); // Mrežna črta
                    gc.strokeRect(i * CELL_SIZE, j * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                }
            }
        }
    }

    private Color getColor(double temp) {

        double tempMin = 0.0;
        double tempMax = 100.0;
        double hueMin = (temp > 40) ? 120 : 240;
        double hueMax = (temp > 50) ? 0 : 110;

        double hue =  hueMin + ((temp - tempMin) / (tempMax - tempMin)) * (hueMax - hueMin);
        if(temp > 100) hue = 0;

        return Color.hsb(hue, 1, 1);
    }


    private StackPane getTemperatureLegend() {
        Stop[] stops = new Stop[11]; // 0, 10, 20, ..., 100

        for (int i = 0; i <= 10; i++) {
            double offset = i * 0.1;
            Color color = getColor(i * 10);
            stops[i] = new Stop(offset, color);
        }

        LinearGradient gradient = new LinearGradient(0, 1, 0, 0, true, null, stops);
        Rectangle gradientBar = new Rectangle(30, 500, gradient);
        gradientBar.setStroke(Color.BLACK);
        gradientBar.setStrokeWidth(1);

        ObservableList<Text> labels = FXCollections.observableArrayList();
        for (int i = 0; i <= 100; i += 10) {
            Text tempLabel = new Text(String.valueOf(i));
            tempLabel.setFill(Color.BLACK);
            tempLabel.setTranslateX(40);
            tempLabel.setTranslateY(250 - (i * 5));
            labels.add(tempLabel);
        }

        StackPane legendPane = new StackPane();
        legendPane.getChildren().addAll(gradientBar);
        legendPane.getChildren().addAll(labels);
        return legendPane;
    }



    public Canvas initializeUI(Stage stage, HeatSimLogic logic, int w, int h) {
        GRID_WIDTH = w;
        GRID_HEIGHT = h;

        double max = Math.max(GRID_WIDTH, GRID_HEIGHT);
        CELL_SIZE = 500 / max;
        if ((int) CELL_SIZE <= 1) CELL_SIZE = 1;

        Canvas gridCanvas = new Canvas(GRID_WIDTH * CELL_SIZE, GRID_HEIGHT * CELL_SIZE);
        GraphicsContext gc = gridCanvas.getGraphicsContext2D();
        drawGrid(gc, logic.getGrid());

        StackPane root = new StackPane();
        //root.getChildren().add(gridCanvas);

        //gradient bar
        StackPane legend = getTemperatureLegend();
        legend.setTranslateX((GRID_WIDTH * CELL_SIZE) / 2 + 50); // Premakni desno od mreže
        legend.setTranslateY(0); // Poravnano z mrežo

        StackPane numbers = new StackPane();


        //numbers.setTranslateX(-((GRID_WIDTH * CELL_SIZE) / 2) - 15);
        //numbers.setTranslateY(-((GRID_HEIGHT * CELL_SIZE) / 2) + (CELL_SIZE / 2));

        numbers.setTranslateX(- ((GRID_WIDTH * CELL_SIZE) / 2));
        numbers.setTranslateY(((GRID_HEIGHT * CELL_SIZE) / 2));

        Text zero = new Text("0");
        zero.setTranslateY(MARGIN_BOTTOM_LABELS);
        zero.setTranslateX(-MARGIN_LEFT_LABELS);

        ObservableList<Text> leftLabels = FXCollections.observableArrayList();
        ObservableList<Text> bottomLabels = FXCollections.observableArrayList();
        drawLegend(leftLabels, bottomLabels);

        numbers.getChildren().add(zero);
        numbers.getChildren().addAll(leftLabels);
        numbers.getChildren().addAll(bottomLabels);

        gridCanvas.setFocusTraversable(true);


        //--------------- USER HAS THE OPTION TO CLICK AND HEAT UP THAT POINT - PARALLEL ----------------------------
        AtomicBoolean isMousePressed = new AtomicBoolean(false);
        AtomicInteger cellX = new AtomicInteger();
        AtomicInteger cellY = new AtomicInteger();

        gridCanvas.setOnMousePressed(event -> {
            double mouseX = event.getX();
            double mouseY = event.getY();

            cellX.set((int) (mouseX / CELL_SIZE));
            cellY.set((int) (mouseY / CELL_SIZE));


            if (cellX.get() >= 0 && cellX.get() < GRID_WIDTH && cellY.get() >= 0 && cellY.get() < GRID_HEIGHT) {

                logic.clickedCellX = cellX.get();
                logic.clickedCellY = cellY.get();
            }
            if (event.isPrimaryButtonDown()) {
                isMousePressed.set(true);
            }
        });

        gridCanvas.setOnMouseReleased(event -> {
            logic.clickedCellX = -1;
            logic.clickedCellY = -1;
            isMousePressed.set(false);
        });

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (isMousePressed.get()) {
                    logic.heatUpCell(cellY.get(), cellX.get());
                    drawGrid(gc, logic.getGrid());
                }
            }
        };
        timer.start();
// ----------------------------------------------------------------------------------------------------------



        root.getChildren().add(legend);
        root.getChildren().add(numbers);



        root.getChildren().add(gridCanvas);

        Scene scene = new Scene(root, 800, 600);
        stage.setTitle("Heat Simulation");
        stage.setScene(scene);
        stage.show();
        return gridCanvas;
    }
}
