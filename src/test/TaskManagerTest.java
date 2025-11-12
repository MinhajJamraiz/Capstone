package test;

import static org.junit.jupiter.api.Assertions.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.*;

import application.DHLWarehouseSystem;
import application.Dock;
import log_manager.LogManager;
import task_manager.*;

class TaskManagerTest {

    private static Path baseDir = Paths.get("test-logs");
    private LogManager logManager;
    private DHLWarehouseSystem system;
    private TaskManager taskManager;
    private Dock loadingDock , unloadingDock;

    @BeforeEach
    void setup() {
        logManager = new LogManager(baseDir);
        system = new DHLWarehouseSystem();
        taskManager = new TaskManager(system, logManager);
        loadingDock = new Dock("LD-1", "GERMANY" ,false);
        unloadingDock = new Dock("UD-1", "GERMANY" ,true);
        system.robots.clear(); // ensure no leftover robots
    }

    // 🧪 Test 1 — Create Unload Task
    @Test
    void testCreateUnloadTask() {
        Task t = taskManager.createUnLoadTask(unloadingDock);
        assertNotNull(t, "Unload task should be created");
        assertTrue(t instanceof UnloadTask, "Should create UnloadTask type");
        assertTrue(t.getTaskId().startsWith("T-"), "Task ID should start with T-");
    }

    // 🧪 Test 2 — Create Load Task
    @Test
    void testCreateLoadTask() {
        Task t = taskManager.createloadTask("P-101", loadingDock);
        assertNotNull(t, "Load task should be created");
        assertTrue(t instanceof LoadTask, "Should create LoadTask type");
        assertEquals(TaskType.LOAD, t.getType(), "Task type must be LOAD");
    }

    // 🧪 Test 3 — Update Task Status Logs Correctly
    @Test
    void testUpdateTaskStatus() {
        Task task = taskManager.createloadTask("PX-2", loadingDock);
        taskManager.updateTaskStatus(task.getTaskId(), TaskStatus.IN_PROGRESS);

        // Verify the status update was logged
        Optional<String> logText = logManager.readLog("SYSTEM", null);
        assertTrue(logText.isPresent(), "System log should exist");
        assertTrue(logText.get().contains("Task " + task.getTaskId() + " status updated"),
                "Log should contain task status update");
    }

    @AfterEach
    void cleanup() {
        logManager.closeAll();
    }
}
