package robot_manager;

import application.DHLWarehouseSystem;
import application.Location;
import log_manager.LogManager;

//import application.DHLWareHouseMainWithLogs.ChargingStation;
//import application.DHLWareHouseMainWithLogs.DHLWarehouseSystem;
//import application.DHLWareHouseMainWithLogs.Location;
//import application.DHLWareHouseMainWithLogs.LogManager;

public class LogisticsRobot extends AbstractRobot {
    public LogisticsRobot(String id, DHLWarehouseSystem system, LogManager logManager) {
        super(id, system, logManager);
    }

    public void pickParcel(String parcelId) {
        logManager.logRobot(robotId, "picking parcel " + parcelId);
    }

    public void deliverParcel(Location dest) {
        logManager.logRobot(robotId, "delivering to " + dest);
    }

    public void goToCharge(ChargingStation cs) {
        boolean accepted = cs.occupy(this);
        if (accepted) {
            status = "CHARGING@" + cs.getId();
            logManager.logRobot(robotId, "started charging at " + cs.getId());
        } else {
            logManager.logRobot(robotId, "failed to occupy charging station " + cs.getId());
        }
    }

    @Override
    public void moveTo(Location location) {
        // simple: no pathfinding required in this simulation
        logManager.logRobot(robotId, "moving to " + location);
    }
}
