package storage_manager;

public class Parcel {
    private final String parcelId;
    private final double weight;
    private final String destination;
    private final int priority;

    public Parcel(String parcelId, double weight, String destination, int priority) {
        this.parcelId = parcelId;
        this.weight = weight;
        this.destination = destination;
        this.priority = priority;
    }

    public String getParcelId() {
        return parcelId;
    }

    public double getWeight() {
        return weight;
    }

    public String getDestination() {
        return destination;
    }

    public int getPriority() {
        return priority;
    }
}
