package application;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

// helper row classes for tables
public class RobotRow {
    private final SimpleStringProperty id;
    private final SimpleStringProperty status;
    private final SimpleDoubleProperty battery;
    public RobotRow(String id, String status, double battery) {
        this.id = new SimpleStringProperty(id);
        this.status = new SimpleStringProperty(status);
        this.battery = new SimpleDoubleProperty(Math.round(battery*10.0)/10.0);
    }
    public String getId() { return id.get(); }
    public String getStatus() { return status.get(); }
    public double getBattery() { return battery.get(); }
}
