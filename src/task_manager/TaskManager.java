package task_manager;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import application.DHLWarehouseSystem;
import log_manager.LogManager;
import robot_manager.LogisticsRobot;
import application.Dock;


public class TaskManager implements ITaskAssignable {
    private final Queue<Task> tasks = new ArrayDeque<>();
    private final DHLWarehouseSystem system;
    private final LogManager logManager;
    private final AtomicInteger taskCounter = new AtomicInteger(1);
    private int i=0;

    public TaskManager(DHLWarehouseSystem system, LogManager logManager) {
        this.system = system;
        this.logManager = logManager;
    }
    //docking createLoadTask
    public Task createUnLoadTask(Dock dock) {
        String tid = "T-" + taskCounter.getAndIncrement();
        String[] destinations = {"FRANCE", "GERMANY", "BELGIUM", "AUSTRIA", "DENMARK"};
        //return destinations[new Random().nextInt(destinations.length)];
        int parcel_dest_id = new Random().nextInt(destinations.length);
        String parcel_dest = destinations[parcel_dest_id];
        String pid = system.storageManager.generateNewParcelId(parcel_dest/*dock.getDestination(*/);
        Task t = new UnloadTask(tid, pid, dock);
        tasks.add(t);
        logManager.logSystem("Created task " + tid + " UNLOAD " + pid + " for " + dock.getDestination());
        return t;
    }

    //docking createUnloadTask
    public Task createloadTask(String parcelId, Dock dock) {
        String tid = "T-" + taskCounter.getAndIncrement();
        Task t = new LoadTask(tid, parcelId, dock);
        tasks.add(t);
        logManager.logSystem("Created task " + tid + " LOAD " + parcelId + " at " + dock.getId());
        return t;
    }
    

    public void assignTask(LogisticsRobot robot, Task task) {
        if (robot.isIdle()) {
            robot.assignTask(task);
            logManager.logSystem("Assigned task " + task.getTaskId() + " to robot " + robot.getId());
        } else {
            logManager.logSystemWarning("Tried to assign task to busy robot " + robot.getId());
        }
    }
    @Override
    public void assignTask(Task task) {
        // Default implementation: assign task to first idle robot
        Optional<LogisticsRobot> optRobot = system.robots.stream().filter(LogisticsRobot::isIdle).findFirst();
        optRobot.ifPresent(robot -> robot.assignTask(task));
    }


    public void updateTaskStatus(String taskId, TaskStatus status) {
        logManager.logSystem("Task " + taskId + " status updated to " + status);
    }
    public void processPendingTasks() {
        // simple assignment policy: assign tasks to nearest idle robot
        if (tasks.isEmpty() /*|| i <=5*/) {
        	//i++;
        	return;
        }
        synchronized (system.robots) {
            Iterator<Task> it = tasks.iterator();
            while (it.hasNext()) {
                Task t = it.next();
                Optional<LogisticsRobot> optRobot = system.robots.stream().filter(LogisticsRobot::isIdle).findFirst();
                if (optRobot.isPresent()/*&& i<=5*/) {
                    LogisticsRobot r = optRobot.get();
                    if (!(r.getBatteryLevel() <12 && r.getStatus().equals("IDLE"))) {
                    assignTask(r, t);
                    it.remove();
                    }
                    //i++;
                    //logManager.logSystem("value of i is "+i);
                } else {
                    // no idle robot
                    break;
                }
            }
        }
    }

    public List<Task> getQueuedTasks() {
        return new ArrayList<>(tasks);
    }
}

