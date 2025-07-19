package heatsim.ui;

import heatsim.settings.Settings;
import heatsim.simulation.Cell;
import heatsim.simulation.VisualGrid;
import heatsim.simulation.Logic;
import heatsim.simulation.Position;
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

    private static double CELL_SIZE;
    VisualGrid grid;

    Logic logic;
    Stage stage;
    Canvas gridCanvas, borderCanvas;
    GraphicsContext gridGC, borderGC;


    AtomicBoolean isMousePressed = new AtomicBoolean(false);
    AtomicInteger cellX = new AtomicInteger();
    AtomicInteger cellY = new AtomicInteger();
    Cell clickedCell = null;

    public Visualizer(Stage stage, Logic logic) {
        this.stage = stage;
        this.logic = logic;
        CELL_SIZE = getCellSize();
        grid = (VisualGrid) logic.getGrid();
    }




    public void drawGrid() {
        for (int i = 0; i < Settings.GRID_WIDTH; i++) {
            for (int j = 0; j < Settings.GRID_HEIGHT; j++) {
                Cell cell = grid.getCell(new Position(i, j));
                if(!cell.isDirty()) continue;

                double x = i * CELL_SIZE;
                double y = j * CELL_SIZE;
                double size = CELL_SIZE + ((CELL_SIZE < Settings.MIN_CELL_SIZE_TO_DRAW_BORDER) ? 0.5 : 0);

                gridGC.setFill(getTemperatureColor(cell.getTemperature()));
                gridGC.fillRect(x, y, size, size);
                if(cell.getTemperature() < Settings.HEAT_RETENTION_THRESHOLD) cell.clearDirty();
            }
        }
    }

    private Color getTemperatureColor(double temp) {

        double tempMin = 0.0;
        double tempMax = 100.0;
        double hueMin = (temp > 40) ? 120 : 240;
        double hueMax = (temp > 50) ? 0 : 110;

        double hue = hueMin + ((temp - tempMin) / (tempMax - tempMin)) * (hueMax - hueMin);
        if (temp > 100) hue = 0;

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
            tempLabel.setTranslateY((Settings.GRID_SIZE / 2.) - (i * (Settings.GRID_SIZE / 100.)));
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

    private double getCellSize() {
        double max = Math.max(Settings.GRID_WIDTH, Settings.GRID_HEIGHT);
        return Settings.GRID_SIZE / max;
    }

    private StackPane getTemperatureLegend() {
        StackPane legend = getTemperatureColorBar();
        legend.setTranslateX((Settings.GRID_WIDTH * CELL_SIZE) / 2 + Settings.GRID_BAR_MARGIN);
        legend.setTranslateY(0);
        return legend;
    }

    private void initCanvases() {
        double width = Settings.GRID_WIDTH * CELL_SIZE;
        double height = Settings.GRID_HEIGHT * CELL_SIZE;

        gridCanvas = new Canvas(width, height);
        borderCanvas = new Canvas(width, height);

        gridGC = gridCanvas.getGraphicsContext2D();
        borderGC = borderCanvas.getGraphicsContext2D();

        drawBorderGrid();

    }

    private void setupGridClickListeners() {
        borderCanvas.setOnMousePressed(this::mousePressed);
        borderCanvas.setOnMouseReleased(event -> {
            if (Settings.RECALCULATE_CLICKED_CELLS && clickedCell != null) clickedCell.setClicked(false);
            isMousePressed.set(false);
        });
    }

    private StackPane createRootPane() {
        StackPane root = new StackPane();
        root.getChildren().add(getTemperatureLegend());
        root.getChildren().add(new LabelGenerator(CELL_SIZE).getNumberLabels());
        root.getChildren().add(gridCanvas);
        root.getChildren().add(borderCanvas);

        return root;
    }

    private void drawBorderGrid() {
        if (CELL_SIZE > Settings.MIN_CELL_SIZE_TO_DRAW_BORDER) {
            borderGC.setStroke(Color.BLACK);

            for (int i = 0; i <= Settings.GRID_WIDTH; i++) {
                double x = i * CELL_SIZE + 0.25;
                borderGC.strokeLine(x, 0, x, Settings.GRID_HEIGHT * CELL_SIZE);
            }

            for (int j = 0; j <= Settings.GRID_HEIGHT; j++) {
                double y = j * CELL_SIZE + 0.25;
                borderGC.strokeLine(0, y, Settings.GRID_WIDTH * CELL_SIZE, y);
            }
        }
    }

    public void initializeUI() {
        initCanvases();
        setupGridClickListeners();



        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                heatCellIfMousePressed();
            }
        };
        timer.start();

        initializeStage(createRootPane());
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

        clickedCell = grid.getCellWithinBorder(cellX.get(), cellY.get());

        isMousePressed.set(event.isPrimaryButtonDown());
        if (clickedCell == null) return;
        clickedCell.setClicked(true);
    }

    private void heatCellIfMousePressed() {
        if (!isMousePressed.get()) return;
        Cell cell = grid.getCellWithinBorder(cellX.get(), cellY.get());
        if (cell == null) return;
        grid.heatUpCell(cell);
        drawGrid();
    }
}
