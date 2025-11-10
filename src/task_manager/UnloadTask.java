package task_manager;

import java.util.Optional;
import java.util.Random;

import application.Dock;
import application.Location;
import robot_manager.LogisticsRobot;
import storage_manager.Parcel;

//import application.DHLWareHouseMainWithLogs.Dock;

//Docking UnlaodTask
public class UnloadTask extends Task {
    private final Dock unloadingDock;
    private String parcel_scanned = "";
    public UnloadTask(String taskId, String parcelId, Dock dock) {
        super(taskId, TaskType.UNLOAD, parcelId);
        this.unloadingDock = dock;
        this.parcel_scanned = parcelId;
    }

    @Override
    public void execute(LogisticsRobot robot) {
        markInProgress();
        robot.getSystem().getLogManager().logRobot(robot.getId(), "[Task] executing UNLOAD " + parcelId + " at " + unloadingDock.getId());
        // simulate scanning and storing
        try {
			Thread.sleep(6000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        Parcel p = new Parcel(parcelId, 1.0, parcelId/*randomDestination()*/, 1);
        Optional<Location> loc = robot.getSystem().storageManager.storeParcel(p);
        if (loc.isPresent()) markComplete(); else markFailed();
    }

    private String randomDestination() {
        String[] destinations = {"FRANCE", "GERMANY", "BELGIUM", "AUSTRIA", "DENMARK"};
        return destinations[new Random().nextInt(destinations.length)];
    }
}
