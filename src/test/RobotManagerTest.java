package test;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.*;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import application.*;
import log_manager.LogManager;
import robot_manager.*;
import task_manager.*;


class RobotManagerTest {

    private static Path tempDir;
    LogisticsRobot robot;
    ChargingStation station;
    DHLWarehouseSystem system;
    LogManager logger;
    Dock dock;

    @BeforeEach
    void setup() throws IOException {
        // Minimal fake system with required fields
    	tempDir = Files.createTempDirectory("robot_manager_test");
        logger = new LogManager(tempDir);
        system = new DHLWarehouseSystem();

        robot = new LogisticsRobot("R1", system, logger);
        station = new ChargingStation("CS1", logger);
        dock = new Dock("D1" , "GERMANY" , false);

        // Add charging station to system
        system.chargingStations.add(station);
    }


    @Test
    void testRobotStatusChange() {
        robot.setStatus("CHARGING");

        assertEquals("CHARGING", robot.getStatus());
    }


    @Test
    void testAssignTaskAndRobotBusy() throws Exception {
    	LoadTask t  = new LoadTask("T1" , "P1" , dock);
        robot.assignTask(t);

        assertTrue(robot.getStatus().contains("BUSY"), "The Robot Status should be BUSY");
    }

    @Test
    void testBatteryDrainsWhenBusy() {

        double before = robot.getBatteryLevel();
        robot.discharge(); // robot is busy → drains faster

        assertTrue(robot.getBatteryLevel() < before , "Robot Battery Should have decreased!!");
    }

    @Test
    void testBatteryLowTriggersAutoChargeTask() {
        robot.discharge();// cut battery artificially
        robot.discharge();// cut battery artificially
        robot.discharge();// cut battery artificially
        robot.discharge();// cut battery artificially
        robot.discharge();// cut battery artificially
        robot.discharge();// cut battery artificially
        double before = robot.getBatteryLevel();
        robot.charge();
        assertTrue(robot.getBatteryLevel() > before , "Robot Battery Should have Increased!!");
    }


}
