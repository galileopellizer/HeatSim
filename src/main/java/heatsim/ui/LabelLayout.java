package heatsim.ui;

import java.util.function.Function;

public class LabelLayout {
    int step;
    int gridSize;
    Function<Integer, Double> xMargin;
    Function<Integer, Double> yMargin;

    public LabelLayout(int step, int gridSize, Function<Integer, Double> xMargin, Function<Integer, Double> yMargin) {
        this.step = step;
        this.gridSize = gridSize;
        this.xMargin = xMargin;
        this.yMargin = yMargin;

    }
}
