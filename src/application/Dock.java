package application;
//----------------------------------
/// Addin Docks loginc
//------------------------------
public class Dock {
    private final String id;
    private final String destination; // For loading docks
    private final boolean isUnloadingDock; // true = unloading, false = loading

    public Dock(String id, String destination, boolean isUnloadingDock) {
        this.id = id;
        this.destination = destination;
        this.isUnloadingDock = isUnloadingDock;
    }

    public String getId() { return id; }
    public String getDestination() { return destination; }
    public boolean isUnloadingDock() { return isUnloadingDock; }
}
