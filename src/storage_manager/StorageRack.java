package storage_manager;

import java.util.ArrayList;
import java.util.List;

//import application.DHLWareHouseMainWithLogs.StorageSlot;

public class StorageRack {
    private final String id;
    private final List<StorageSlot> slots = new ArrayList<>();

    public StorageRack(String id) {
        this.id = id;
    }

    public void addSlot(StorageSlot slot) {
        slots.add(slot);
    }

    public List<StorageSlot> getSlots() {
        return slots;
    }

    public String getId() {
        return id;
    }
}
