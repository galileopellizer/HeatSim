module heatsim {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires jdk.unsupported.desktop;
    requires mpj;

    exports heatsim.simulation;
    opens heatsim.simulation to javafx.fxml;
    exports heatsim.ui;
    opens heatsim.ui to javafx.fxml;
    exports heatsim.settings;
    opens heatsim.settings to javafx.fxml;
}