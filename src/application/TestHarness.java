package application;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import log_manager.LogManager;

//---------------------
// Simple Test Harness
// ---------------------
public class TestHarness {
    public static void runAll(LogManager logManager) {
        System.out.println("Running simple logging tests...");
        testLogCreation(logManager);
        testArchiveAndMoveDelete(logManager);
        System.out.println("Logging tests completed. Check logs/ folder.");
    }

    private static void testLogCreation(LogManager logManager) {
        // create variety of logs
        logManager.logSystem("TEST: system startup sample log");
        logManager.logRobot("R-1", "TEST: robot R-1 sample log line 1");
        logManager.logRobot("R-2", "TEST: robot R-2 sample log line 1");
        logManager.logChargingStation("CS-1", "TEST: CS-1 sample log line 1");
        logManager.logStorage("TEST: storage log entry sample");
        // also write many lines to create content
        for (int i = 0; i < 3; i++) {
            logManager.logRobot("R-1", "Heartbeat " + i);
        }
    }

    private static void testArchiveAndMoveDelete(LogManager logManager) {
        // find logs with ROBOT-R-1 and archive them
        List<Path> matches = logManager.listLogs("(?i)ROBOT-R-1");
        List<String> names = new ArrayList<>();
        for (Path p : matches) names.add(p.getFileName().toString());
        if (!names.isEmpty()) {
            logManager.archiveLogs(names, "test_robot_r1_archive.zip");
            // move one of them (if exists)
            if (!names.isEmpty()) {
                logManager.moveLog(names.get(0));
            }
        }
        // delete a non-existing file to show warning path
        logManager.deleteLog("nonexistent_0000-00-00.log");
    }
}
