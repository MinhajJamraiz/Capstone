package task_manager;

import java.util.Optional;

import robot_manager.ChargingStation;
import robot_manager.LogisticsRobot;
import task_manager.Task;

//import application.DHLWareHouseMainWithLogs.ChargingStation;

public class ChargeTask extends Task {
    public ChargeTask(String taskId, String parcelId) { super(taskId, TaskType.CHARGE, parcelId); }
    @Override
    public void execute(LogisticsRobot robot) {
        markInProgress();
        robot.getSystem().getLogManager().logRobot(robot.getId(), "[Task] executing CHARGE");
        // find free charging station
        robot.setStatus("UNCHARGED");
        Optional<ChargingStation> cs = robot.getSystem().chargingStations.stream().filter(ChargingStation::isAvailable).findFirst();

        if (cs.isPresent()) {
            robot.goToCharge(cs.get());
            
            if (robot.getBatteryLevel() > 98) {
            markComplete();
            }
        } else {
            markFailed();
        }
    }
}
