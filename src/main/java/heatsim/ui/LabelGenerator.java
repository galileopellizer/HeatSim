package heatsim.ui;

import heatsim.settings.Settings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class LabelGenerator {

    private final double CELL_SIZE;

    public LabelGenerator(double CELL_SIZE) {
        this.CELL_SIZE = CELL_SIZE;
    }

    public StackPane getNumberLabels() {
        StackPane numbers = new StackPane();

        numbers.setTranslateX(-((Settings.GRID_WIDTH * CELL_SIZE) / 2));
        numbers.setTranslateY(((Settings.GRID_HEIGHT * CELL_SIZE) / 2));

        numbers.getChildren().addAll(getGridLabels());
        return numbers;
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
        return -((x) * CELL_SIZE) + (CELL_SIZE / 2);
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

        int step = Math.max(calculateStep(Settings.GRID_WIDTH), calculateStep(Settings.GRID_HEIGHT));
        LabelLayout labelX = new LabelLayout(step, Settings.GRID_WIDTH, this::computeXLabelXOffset, this::computeXLabelYOffset);
        LabelLayout labelY = new LabelLayout(step, Settings.GRID_HEIGHT, this::computeYLabelXOffset, this::computeYLabelYOffset);
        labels.addAll(generateGridLabels(labelX));
        labels.addAll(generateGridLabels(labelY));
        labels.add(getLabelZero());
        return labels;
    }



    private int calculateStep(int gridDimension) {
        int maxLabels = Settings.GRID_SIZE / Settings.MARGIN_BETWEEN_LABELS;
        double rawStep = (double) gridDimension / maxLabels;
        return niceNumber(rawStep);
    }

    private int niceNumber(double value) {
        int exponent = (int) Math.floor(Math.log10(value));
        double fraction = value / Math.pow(10, exponent);

        double niceFraction;
        if (fraction <= 1)
            niceFraction = 1;
        else if (fraction <= 2)
            niceFraction = 2;
        else if (fraction <= 5)
            niceFraction = 5;
        else
            niceFraction = 10;

        return (int) (niceFraction * Math.pow(10, exponent));
    }

}
