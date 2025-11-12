package robot_manager;

import log_manager.LogManager;

//import application.DHLWareHouseMainWithLogs.LogManager;

public class ChargingStation implements IEquipment {
    private final String id;
    private boolean occupied = false;
    private LogisticsRobot occupant = null;
    private final LogManager logManager;

    public ChargingStation(String id, LogManager logManager) {
        this.id = id;
        this.logManager = logManager;
        logManager.logChargingStation(id, "Created charging station " + id);
    }

    public String getId() { return id; }
    public String getStatus() { return occupied ? "OCCUPIED" : "FREE"; }

    public synchronized boolean isAvailable() { return !occupied; }

    public synchronized boolean occupy(LogisticsRobot robot) {
        if (occupied) return false;
        occupied = true;
        occupant = robot;
        logManager.logChargingStation(id, "ChargingStation " + id + " occupied by " + robot.getId());
        return true;
    }

    public synchronized void release() {
        if (occupied) {
            logManager.logChargingStation(id, "ChargingStation " + id + " released by " + (occupant==null?"unknown":occupant.getId()));
            occupied = false;
            occupant = null;
        }
    }

    public synchronized boolean isOccupiedBy(/*LogisticsRobot robot*/AbstractRobot robot) {
        return occupied && occupant == robot;
    }
}
