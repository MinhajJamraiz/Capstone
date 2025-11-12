package test;

import log_manager.LogManager;

import org.junit.jupiter.api.*;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class LogManagerTest {

    private static Path tempDir;
    private LogManager logManager;

    @BeforeAll
    static void setupOnce() throws IOException {
        // Create a temporary directory for testing (isolated environment)
        tempDir = Files.createTempDirectory("logmanager_test_");
    }

    @BeforeEach
    void setup() {
        // Initialize a new LogManager for each test
        logManager = new LogManager(tempDir);
    }

    @AfterEach
    void cleanup() {
        // Close all open writers/streams after each test
        logManager.closeAll();
    }

    @AfterAll
    static void tearDownOnce() throws IOException {
        // Cleanup test directory after all tests
        Files.walk(tempDir)
            .sorted(Comparator.reverseOrder())
            .forEach(path -> path.toFile().delete());
    }

    // 🧪 TEST 1 — Writing and reading a system log
    @Test
    void testWriteAndReadSystemLog() {
        logManager.logSystem("System log test entry");

        Optional<String> result = logManager.readLog("SYSTEM", null);
        assertTrue(result.isPresent(), "System log should be readable");
        assertTrue(result.get().contains("System log test entry"), "Log content should match");
    }

    // 🧪 TEST 2 — Listing created logs
    @Test
    void testListLogs() {
        logManager.logRobot("R1", "Robot log test");
        List<Path> files = logManager.listLogs("ROBOT");
        assertFalse(files.isEmpty(), "Should find at least one robot log file");
        assertTrue(files.get(0).getFileName().toString().contains("ROBOT"), "Filename should contain 'ROBOT'");
    }

    // 🧪 TEST 3 — Deleting a log file
    @Test
    void testDeleteLogFile() {
        logManager.logStorage("Storage test log");
        List<Path> logs = logManager.listLogs("STORAGE");
        assertFalse(logs.isEmpty(), "A STORAGE log should exist");

        String fileName = logs.get(0).getFileName().toString();
        boolean deleted = logManager.deleteLog(fileName);
        assertTrue(deleted, "Log file should be deleted successfully");
        assertFalse(Files.exists(tempDir.resolve(fileName)), "File should no longer exist");
    }



    // 🧪 TEST 4 — Archiving multiple log files into ZIP
    @Test
    void testArchiveLogs() {
        // Create two logs
        logManager.logSystem("Archive test 1");
        logManager.logStorage("Archive test 2");

        List<Path> logs = logManager.listLogs("");
        List<String> filenames = new ArrayList<>();
        for (Path p : logs) filenames.add(p.getFileName().toString());

        Optional<Path> archive = logManager.archiveLogs(filenames, "test_archive.zip");
        assertTrue(archive.isPresent(), "Archive should be created");
        assertTrue(Files.exists(archive.get()), "ZIP file should exist in archive folder");
        assertTrue(archive.get().toString().endsWith(".zip"), "Archive should have .zip extension");
    }
}
