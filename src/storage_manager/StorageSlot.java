package storage_manager;

//import application.DHLWareHouseMainWithLogs.Parcel;

public class StorageSlot {
    private final String id;
    private Parcel parcel;

    public StorageSlot(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public boolean isOccupied() {
        return parcel != null;
    }

    public Parcel getParcel() {
        return parcel;
    }

    public void setParcel(Parcel parcel) {
        this.parcel = parcel;
    }
}
