package heatsim.ui;

import heatsim.settings.Settings;
import heatsim.simulation.Cell;
import heatsim.simulation.Grid;
import heatsim.simulation.Logic;
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


import javafx.scene.input.MouseEvent;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Visualizer {

    private static int GRID_WIDTH;
    private static int GRID_HEIGHT;

    private static  double  CELL_SIZE;

    Logic logic;
    Stage stage;
    Canvas canvas;
    GraphicsContext gc;
    AtomicBoolean isMousePressed = new AtomicBoolean(false);
    AtomicInteger cellX = new AtomicInteger();
    AtomicInteger cellY = new AtomicInteger();
    Cell clickedCell = null;

    public Visualizer(Stage stage, Logic logic) {
        this.stage = stage;
        this.logic = logic;
        GRID_HEIGHT = Settings.GRID_HEIGHT;
        GRID_WIDTH = Settings.GRID_WIDTH;
        //canvas = this.initializeUI();
        //this.gc = canvas.getGraphicsContext2D();
    }


    private int calculateStep(int gridSize) {
        int gridLength= (gridSize * (int)CELL_SIZE);

        int labelMargin = gridLength / (gridSize);

        if(labelMargin >= Settings.MARGIN_BETWEEN_LABELS) return 1;
        int step = 5;

        //steps vrednosti 1, 5, 10
        while(labelMargin < Settings.MARGIN_BETWEEN_LABELS) {


            if(step > gridSize) {
                step = gridSize;
                break;
            }
            labelMargin = gridLength / (gridSize / step);
            step += (labelMargin < Settings.MARGIN_BETWEEN_LABELS) ? 5 : 0;

        }
        return step;
    }

    public void drawGrid() {
        Grid grid = logic.grid;
        for (int i = 0; i < GRID_WIDTH; i++) {
            for (int j = 0; j < GRID_HEIGHT; j++) {

                gc.setFill(getTemperatureColor(grid.getCell(i, j).getTemperature())); // Začetna barva
                gc.fillRect(i * CELL_SIZE, j * CELL_SIZE, CELL_SIZE, CELL_SIZE);

                if(CELL_SIZE > 3) {
                    gc.setStroke(Color.BLACK); // Mrežna črta
                    gc.strokeRect(i * CELL_SIZE, j * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                }
                /*
                // Draw temperature as text, debugging
                gc.setFill(Color.BLACK);
                gc.setFont(new Font(10));
                gc.fillText(
                        String.format("%.0f", grid.getCell(i, j, false).getTemperature()),
                        i * CELL_SIZE + 2,
                        j * CELL_SIZE + 12
                );
                */
            }
        }
    }

    private Color getTemperatureColor(double temp) {

        double tempMin = 0.0;
        double tempMax = 100.0;
        double hueMin = (temp > 40) ? 120 : 240;
        double hueMax = (temp > 50) ? 0 : 110;

        double hue =  hueMin + ((temp - tempMin) / (tempMax - tempMin)) * (hueMax - hueMin);
        if(temp > 100) hue = 0;

        return Color.hsb(hue, 1, 1);
    }

    private Stop[] generateGradientStops() {
        Stop[] stops = new Stop[10];
        for (int i = 0; i < 10; i++) {
            double offset = i * 0.1;
            Color color = getTemperatureColor(i * 10);
            stops[i] = new Stop(offset, color);
        }
        return stops;
    }

    private ObservableList<Text> generateTemperatureBarLabels() {
        ObservableList<Text> labels = FXCollections.observableArrayList();
        for (int i = 0; i <= 100; i += 10) {
            Text tempLabel = new Text(String.valueOf(i));
            tempLabel.setFill(Color.BLACK);
            tempLabel.setTranslateX(Settings.TEMP_BAR_LABELS_MARGIN);
            tempLabel.setTranslateY((Settings.GRID_SIZE / 2) - (i * (Settings.GRID_SIZE / 100)));
            labels.add(tempLabel);
        }
        return labels;
    }

    private Rectangle generateTemperatureGradientBar() {
        LinearGradient gradient = new LinearGradient(0, 1, 0, 0, true, null, generateGradientStops());
        Rectangle gradientBar = new Rectangle(30, Settings.GRID_SIZE, gradient);
        gradientBar.setStroke(Color.BLACK);
        gradientBar.setStrokeWidth(1);
        return gradientBar;
    }

    private StackPane getTemperatureColorBar() {
        StackPane legendPane = new StackPane();
        legendPane.getChildren().addAll(generateTemperatureGradientBar());
        legendPane.getChildren().addAll(generateTemperatureBarLabels());
        return legendPane;
    }




    private void setCellSize() {
        double max = Math.max(GRID_WIDTH, GRID_HEIGHT);
        CELL_SIZE = Settings.GRID_SIZE / max;
    }

    private ObservableList<Text> generateGridLabels(LabelLayout label) {
        ObservableList<Text> labels = FXCollections.observableArrayList();
        for (int i = label.step; i <= label.gridSize; i += label.step) {
            Text text = new Text(String.valueOf(i));
            text.setFill(Color.BLACK);
            text.setTranslateX(label.xMargin.apply(i));
            text.setTranslateY(label.yMargin.apply(i));
            labels.add(text);
        }
        return labels;
    }

    private double computeYLabelYOffset(int x) {
        return -((x) * CELL_SIZE) + (CELL_SIZE/2);
    }
    private double computeYLabelXOffset(int x) {
        return -Settings.Y_LABELS_MARGIN;
    }

    private double computeXLabelXOffset(int x) {
        return CELL_SIZE * x - (CELL_SIZE / 2);
    }

    private double computeXLabelYOffset(int x) {
        return Settings.X_LABELS_MARGIN;
    }

    private Text getLabelZero() {
        Text zero = new Text("0");
        zero.setTranslateY(Settings.X_LABELS_MARGIN);
        zero.setTranslateX(-Settings.Y_LABELS_MARGIN);
        return zero;
    }

    private ObservableList<Text> getGridLabels() {
        ObservableList<Text> labels = FXCollections.observableArrayList();

        int stepX = calculateStep(GRID_WIDTH);
        int stepY = calculateStep(GRID_HEIGHT);
        LabelLayout labelX = new LabelLayout(stepX, GRID_WIDTH, this::computeXLabelXOffset, this::computeXLabelYOffset);
        LabelLayout labelY = new LabelLayout(stepY, GRID_HEIGHT, this::computeYLabelXOffset, this::computeYLabelYOffset);
        labels.addAll(generateGridLabels(labelX));
        labels.addAll(generateGridLabels(labelY));
        labels.add(getLabelZero());
        return labels;
    }

    private StackPane getNumberLabels() {
        StackPane numbers = new StackPane();


        numbers.setTranslateX(- ((GRID_WIDTH * CELL_SIZE) / 2));
        numbers.setTranslateY(((GRID_HEIGHT * CELL_SIZE) / 2));

        numbers.getChildren().addAll(getGridLabels());
        return numbers;
    }

    private StackPane getTemperatureLegend() {
        StackPane legend = getTemperatureColorBar();
        legend.setTranslateX((GRID_WIDTH * CELL_SIZE) / 2 + Settings.GRID_BAR_MARGIN);
        legend.setTranslateY(0);
        return legend;
    }

    public void initializeUI() {
        setCellSize();

        Canvas gridCanvas = new Canvas(GRID_WIDTH * CELL_SIZE, GRID_HEIGHT * CELL_SIZE);
        gc = gridCanvas.getGraphicsContext2D();
        this.drawGrid();

        StackPane root = new StackPane();
        gridCanvas.setFocusTraversable(true);



        gridCanvas.setOnMousePressed(this::mousePressed);

        gridCanvas.setOnMouseReleased(event -> {
            if(Settings.RECALCULATE_CLICKED_CELLS && clickedCell != null) clickedCell.setClicked(false);
            isMousePressed.set(false);
        });

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                heatCellIfMousePressed();
            }
        };
        timer.start();

        root.getChildren().add(getTemperatureLegend());
        root.getChildren().add(getNumberLabels());

        root.getChildren().add(gridCanvas);

        initializeStage(root);
    }

    private void initializeStage(StackPane root) {
        Scene scene = new Scene(root, Settings.APP_WIDTH, Settings.APP_HEIGHT);
        stage.setTitle(Settings.APP_TITLE);
        stage.setScene(scene);
        stage.show();
    }

    private void mousePressed(MouseEvent event) {
        double mouseX = event.getX();
        double mouseY = event.getY();

        cellX.set((int) (mouseX / CELL_SIZE));
        cellY.set((int) (mouseY / CELL_SIZE));

        clickedCell = logic.grid.getCellWithinBorder(cellX.get(), cellY.get());

        isMousePressed.set(event.isPrimaryButtonDown());
        if (clickedCell == null) return;
        clickedCell.setClicked(true);
    }

    private void heatCellIfMousePressed() {
        if (!isMousePressed.get()) return;
        Cell cell = logic.grid.getCellWithinBorder(cellX.get(), cellY.get());
        if(cell == null) return;
        logic.grid.heatUpCell(cell);
        drawGrid();
    }
}
