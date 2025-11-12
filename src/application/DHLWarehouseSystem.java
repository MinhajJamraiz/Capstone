package application;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//import application.DHLWareHouseMainWithLogs.ChargingStation;
//import application.DHLWareHouseMainWithLogs.Dock;
//import application.DHLWareHouseMainWithLogs.LogManager;
//import application.DHLWareHouseMainWithLogs.StorageManager;
//import application.DHLWareHouseMainWithLogs.TaskManager;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class DHLWarehouseSystem {
    public final StorageManager storageManager;
    public final TaskManager taskManager;
    public final List<LogisticsRobot> robots = Collections.synchronizedList(new ArrayList<>());
    public final List<ChargingStation> chargingStations = Collections.synchronizedList(new ArrayList<>());
    private final LogManager logManager;
    private final Timeline simulationTimeline;
    // Adding Docks Logic here
    public final List<Dock> unloadingDocks = new ArrayList<>();
    public final List<Dock> loadingDocks = new ArrayList<>();

    private void initDocks() {
        // Unloading docks (mixed destinations)
        for (int i = 1; i <= 5; i++) {
            unloadingDocks.add(new Dock("UD-" + i, null, true));
        }

        // Loading docks (fixed destination)
        String[] destinations = {"FRANCE", "GERMANY", "BELGIUM", "AUSTRIA", "DENMARK"};
        for (int i = 0; i < 5; i++) {
            loadingDocks.add(new Dock("LD-" + (i + 1), destinations[i], false));
        }
    }
    //--------------------------------------

    public DHLWarehouseSystem() {
        logManager = new LogManager(Paths.get("logs"));
        storageManager = new StorageManager(logManager);
        taskManager = new TaskManager(this, logManager);

        // create few racks, slots
        storageManager.createRacks(5, 6); // 4 racks x 6 slots

        // create charging stations
        for (int i = 0; i < 3; i++) {
            chargingStations.add(new ChargingStation("CS-" + (i + 1), logManager));
        }
        // create Docks
        initDocks();
        // create robots
        for (int i = 0; i < 5; i++) {
            LogisticsRobot r = new LogisticsRobot("R-" + (i + 1), this, logManager);
            robots.add(r);
        }

        simulationTimeline = new Timeline(new KeyFrame(Duration.millis(500), e -> tick()));
        simulationTimeline.setCycleCount(Timeline.INDEFINITE);
    }

    public void startSimulation() {
        logManager.logSystem("Starting simulation");
        simulationTimeline.play();
    }

    public void stopSimulation() {
        logManager.logSystem("Stopping simulation");
        simulationTimeline.stop();
        // gracefully stop robots
        robots.forEach(LogisticsRobot::stop);
        logManager.closeAll();
    }

    private void tick() {
        try {
            // each tick update task manager and robots
            taskManager.processPendingTasks();
            synchronized (robots) {
                for (LogisticsRobot r : robots) {
                    r.tick();
                }
            }
        } catch (Exception ex) {
            logManager.logSystemSevere("Simulation tick error: " + ex.getMessage());
        }
    }

    public LogManager getLogManager() {
        return logManager;
    }
}
