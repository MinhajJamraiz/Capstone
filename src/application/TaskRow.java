package application;

import javafx.beans.property.SimpleStringProperty;

public class TaskRow {
    private final SimpleStringProperty id, type, parcel, status;
    public TaskRow(String id, String type, String parcel, String status) {
        this.id = new SimpleStringProperty(id);
        this.type = new SimpleStringProperty(type);
        this.parcel = new SimpleStringProperty(parcel);
        this.status = new SimpleStringProperty(status);
    }
    public String getId() { return id.get(); }
    public String getType() { return type.get(); }
    public String getParcel() { return parcel.get(); }
    public String getStatus() { return status.get(); }
}
