package heatsim.settings;

public class Settings {

    public final static Mode MODE = Mode.VISUAL;

    public static int RANDOM_SEED = 8247;

    public static int GRID_WIDTH = 460;

    public static  int GRID_HEIGHT = 460;

    public static  int RANDOM_POINTS_NUM = 50;

    public final static  boolean END_SIM_ON_TEMPERATURE_THRESHOLD_REACHED = true;

    public final static  double TEMPERATURE_CHANGE_THRESHOLD = 0.25;

    public final static double CLICK_HEATING_RATE = 0.5;

    public final static int BORDER_SIZE = 1;

    public final static double HEAT_RETENTION_THRESHOLD = 100;

    public final static boolean RECALCULATE_CLICKED_CELLS = true;

    public final static int GRID_SIZE = 500;

    public static final int Y_LABELS_MARGIN = 15;

    public static final int X_LABELS_MARGIN = 15;

    public static final int MARGIN_BETWEEN_LABELS = 50;

    public static final int TEMP_BAR_LABELS_MARGIN = 40;

    public static final int GRID_BAR_MARGIN = 50;

    public static final int MIN_CELL_SIZE_TO_DRAW_BORDER = 3;

    public static final int APP_WIDTH = 800;

    public static final int APP_HEIGHT = 600;

    public static final String APP_TITLE = "Heat Simulation";
}
