package application;
/*
DHL Smart Automated Warehouse - JavaFX Simulation (extended logging)

Author: Muhammad Mohsin Abbasi
*/

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;

import java.io.*;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.*;
import java.util.zip.*;
import java.util.regex.*;

public class DHLWareHouseMainWithLogs extends Application {

    // --- Entry point ---
    public static void main(String[] args) {
        launch(args);
    }

    // Core system instance
    private DHLWarehouseSystem system;
    private HMI hmi;

    @Override
    public void start(Stage primaryStage) throws Exception {
        system = new DHLWarehouseSystem();
        hmi = new HMI(system);

        primaryStage.setTitle("DHL Smart Automated Warehouse - Simulation (Logging Enhanced)");
        primaryStage.setScene(new Scene(hmi.getRoot(), 1200, 780));
        primaryStage.show();

        system.startSimulation();
        hmi.startUIUpdater();

        // run basic tests / demo to create some sample logs
        //TestHarness.runAll(system.getLogManager());
    }

    @Override
    public void stop() throws Exception {
        system.stopSimulation();
        super.stop();
    }
}
