package task_manager;

import java.util.Optional;

import application.Dock;
import robot_manager.LogisticsRobot;
import storage_manager.Parcel;

//import application.DHLWareHouseMainWithLogs.Dock;

public class LoadTask extends Task {
    private final Dock loadingDock;

    public LoadTask(String taskId, String parcelId, Dock dock) {
        super(taskId, TaskType.LOAD, parcelId);
        this.loadingDock = dock;
    }
    

    @Override
    public void execute(LogisticsRobot robot) {
        markInProgress();
        robot.getSystem().getLogManager().logRobot(robot.getId(), "[Task] executing LOAD " + parcelId + " for " + loadingDock.getDestination());
        // find a parcel matching dock destination
        /*Optional<Parcel> opt = robot.getSystem().storageManager.getParcelMap().keySet().stream()
                .map(pid -> robot.getSystem().storageManager.retrieveParcel(pid).orElse(null))
                .filter(p -> p != null && p.getDestination().equals(loadingDock.getDestination()))
                .findFirst();
        if (opt.isPresent()) {
            robot.getSystem().storageManager.retrieveParcel(opt.get().getParcelId());
            markComplete();
        } else {
            markFailed();
        }*/
        Optional<Parcel> p = robot.getSystem().storageManager.retrieveParcel(parcelId);
        //Optional<Location> loc = robot.getSystem().storageManager.storeParcel(p);
        try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        if (p.isPresent()) {
            markComplete();
        } else {
            markFailed();
        }
    }
}

