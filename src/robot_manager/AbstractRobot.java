package robot_manager;

import java.util.concurrent.CompletableFuture;

import application.DHLWarehouseSystem;

import application.Location;

import javafx.application.Platform;
import log_manager.LogManager;
import task_manager.ChargeTask;
import task_manager.ITaskAssignable;
import task_manager.Task;
import task_manager.TaskStatus;

public abstract class AbstractRobot implements IEquipment, ITaskAssignable, IChargeable {
    protected final String robotId;
    protected double batteryLevel = 100.0;
    protected volatile Task currentTask;
    protected String status = "IDLE";
    protected final DHLWarehouseSystem system;
    protected final LogManager logManager;
    protected boolean running = true;
    protected boolean currentTaskRunning=false;
    public AbstractRobot(String robotId, DHLWarehouseSystem system, LogManager logManager) {
        this.robotId = robotId;
        this.system = system;
        this.logManager = logManager;
    }

    public String getId() { return robotId; }
    public String getStatus() { return status; }
    public boolean isIdle() { return currentTask == null && status == "IDLE"; }
    public DHLWarehouseSystem getSystem() { return system; }

    public void assignTask(Task task) {
        this.currentTask = task;
        this.status = "BUSY(" + task.getType() + ")";
        logManager.logRobot(robotId, "Assigned task " + task.getTaskId());
    }

    public void updateTaskStatus(String taskId, TaskStatus status) { /* pass-through */ }

    public void charge() {
        // default charge implementation
        batteryLevel = Math.min(100.0, batteryLevel + 4.0);
        logManager.logRobot(robotId, "charging => battery " + batteryLevel);
    }

    public void discharge() {
        batteryLevel = Math.max(0.0, batteryLevel - 1.0);
    }
    
    public void setStatus(String status) {
    	this.status = status;
    }

    public double getBatteryLevel() { return batteryLevel; }

    public void stop() { running = false; }

    // tick called by system on each simulation frame
    public void tick() {
        try {
            if (!running) return;
            // battery drains if busy or idle slowly
            if (!isIdle()) {
                batteryLevel -= 1.6; // faster drain old 0.6
            } else {
                batteryLevel -= 0.1;
            }
            if (batteryLevel <= 10) {
                // emergency: immediate go to charge
                if (!(currentTask instanceof ChargeTask)) {
                    currentTask = new ChargeTask("AUTO-CHG-" + robotId, null);
                }
            }
         // execute task asynchronously if not already running
            if (currentTask != null && !currentTaskRunning) {
                currentTaskRunning = true;
                Task taskToRun = currentTask;
                CompletableFuture.runAsync(() -> {
                    taskToRun.execute((LogisticsRobot) this);  // this method can now safely sleep or simulate duration
                    // after execution
                    Platform.runLater(() -> {
                        if (taskToRun.getStatus() == TaskStatus.COMPLETED || taskToRun.getStatus() == TaskStatus.FAILED) {
                            currentTask = null;
                            if (status != "UNCHARGED" && status!="CHARGING")
                            status = "IDLE";
                            currentTaskRunning = false;
                            logManager.logRobot(robotId, "Task " + taskToRun.getTaskId() + " finished"+"status:"+status);
                        }
                    });
                });
            }

            // if at charging station, charge
            synchronized (system.chargingStations) {
                for (ChargingStation cs : system.chargingStations) {
                    if (cs.isOccupiedBy(this)) {
                        charge();
                        if (batteryLevel >= 98.0) {
                            cs.release();
                            status="IDLE";
                            currentTask= null;
                            currentTaskRunning = false;
                            logManager.logRobot(robotId, robotId + " finished charging and left station " + cs.getId());
                        }
                    }
                }
            }

            if (batteryLevel < 0) batteryLevel = 0;

        } catch (Exception ex) {
            logManager.logSystemSevere("Tick error on robot " + robotId + ": " + ex.getMessage());
        }
    }

    public abstract void moveTo(Location location);
}

