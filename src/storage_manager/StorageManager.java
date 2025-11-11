package storage_manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import application.Location;
import log_manager.LogManager;

public class StorageManager {
    private final List<StorageRack> racks = new ArrayList<>();
    private final Map<String, Location> parcelMap = new HashMap<>();
    private final LogManager logManager;
    private final AtomicInteger parcelIdCounter = new AtomicInteger(1000);

    public StorageManager(LogManager logManager) {
        this.logManager = logManager;
    }

    public void createRacks(int numRacks, int slotsPerRack) {
        for (int r = 0; r < numRacks; r++) {
            StorageRack rack = new StorageRack("RACK-" + (r + 1));
            for (int s = 0; s < slotsPerRack; s++) {
                rack.addSlot(new StorageSlot(rack.getId() + "-S" + (s + 1)));
            }
            racks.add(rack);
        }
        logManager.logStorage("Created " + numRacks + " racks with " + slotsPerRack + " slots each");
    }

    public Optional<Location> storeParcel(Parcel parcel) {
    	String dest_rack = "";
    	String my_str = parcel.getDestination().substring(2, 4);
    	if (parcel.getDestination().substring(2, 4).equals("FR") )
    		dest_rack = "RACK-1";
    	if (parcel.getDestination().substring(2, 4).equals("GE") )
    		dest_rack = "RACK-2";
    	if (parcel.getDestination().substring(2, 4).equals("AU") )
    		dest_rack = "RACK-3";
    	if (parcel.getDestination().substring(2, 4).equals("DE") )
    		dest_rack = "RACK-4";
    	if (parcel.getDestination().substring(2, 4).equals("BE") )
    		dest_rack = "RACK-5";
        for (StorageRack rack : racks) {
        	if (rack.getId().equals(dest_rack))
            for (StorageSlot slot : rack.getSlots()) {
                if (!slot.isOccupied()) {
                    slot.setParcel(parcel);
                    Location loc = new Location(slot.getId());
                    parcelMap.put(parcel.getParcelId(), loc);
                    logManager.logStorage("Stored parcel " + parcel.getParcelId() + " in " + slot.getId());
                    // also log to robot-specific if a robot is interacting we'll call logManager.logRobot later
                    return Optional.of(loc);
                }
            }
        }
        logManager.logStorageWarning("No available slot to store parcel " + parcel.getParcelId());
        return Optional.empty();
    }

    public Optional<Parcel> retrieveParcel(String parcelId) {
        Location loc = parcelMap.get(parcelId);
        if (loc == null) {
            logManager.logStorageWarning("Parcel not found: " + parcelId);
            return Optional.empty();
        }
        for (StorageRack rack : racks) {
            for (StorageSlot slot : rack.getSlots()) {
                if (slot.getId().equals(loc.getId())) {
                    Parcel p = slot.getParcel();
                    slot.setParcel(null);
                    parcelMap.remove(parcelId);
                    logManager.logStorage("Retrieved parcel " + parcelId + " from " + slot.getId());
                    return Optional.ofNullable(p);
                }
            }
        }
        logManager.logStorageSevere("Inconsistent state retrieving parcel " + parcelId);
        return Optional.empty();
    }
    public String generateNewParcelId(String destination) {
        return "P-" + destination.substring(0, 2).toUpperCase() + "-" + parcelIdCounter.getAndIncrement();
    }

    public List<StorageRack> getRacks() {
        return Collections.unmodifiableList(racks);
    }

    public Map<String, Location> getParcelMap() {
       // return Collections.unmodifiableMap(parcelMap);
    	 return parcelMap;
    }
}
