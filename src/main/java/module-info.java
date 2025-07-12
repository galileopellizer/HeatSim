module com.example.heatsim {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires jdk.unsupported.desktop;

    opens com.galileo.heatsim to javafx.fxml;
    exports com.galileo.heatsim;
}