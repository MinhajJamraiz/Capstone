package application;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

//import application.DHLWareHouseMainWithLogs.HMI.RobotRow;
//import application.DHLWareHouseMainWithLogs.HMI.TaskRow;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class HMI {
    private final DHLWarehouseSystem system;
    private final BorderPane root = new BorderPane();
    private final TableView<RobotRow> robotTable = new TableView<>();
    private final TableView<TaskRow> taskTable = new TableView<>();
    private final TextArea logArea = new TextArea();

    private final ObservableList<RobotRow> robotRows = FXCollections.observableArrayList();
    private final ObservableList<TaskRow> taskRows = FXCollections.observableArrayList();
    private final Map<StorageSlot, Rectangle> slotRectMap = new HashMap<>();// Map to attach slots to rectangle
    private final Map<StorageSlot, Label> slotLabelMap = new HashMap<>();
    
    private Timeline uiTimeline;

    public HMI(DHLWarehouseSystem system) {
        this.system = system;
        buildUI();
    }

    public Parent getRoot() { return root; }

    private void buildUI() {
        // top controls
        HBox top = new HBox(10);
        top.setPadding(new Insets(8));
        Button btnStart = new Button("Start Simulation");
        Button btnStop = new Button("Stop Simulation");
        Button btnCreateLoad = new Button("Create Load Task");
        Button btnCreateUnload = new Button("Create Unload Task (random)");
        Button btnReloadLog = new Button("Reload Log File");
        TextField textField = new TextField();
        
        btnStart.setOnAction(e -> system.startSimulation());
        btnStop.setOnAction(e -> system.stopSimulation());
        btnCreateUnload.setOnAction(e -> {
            Task t = system.taskManager.createUnLoadTask(system.unloadingDocks.get(0));
            taskRows.add(new TaskRow(t.getTaskId(), t.getType().toString(), t.getParcelId(), t.getStatus().toString()));
        });
        btnCreateLoad.setOnAction(e -> {
            // pick a random stored parcel
            Map<String, Location> map = system.storageManager.getParcelMap();
            if (map.isEmpty()) {
                system.getLogManager().logStorageWarning("No parcels to load");
                return;
            }
           // String text_field = system.loading_destination_text;
            String text_field = textField.getText();
            String dest_substring = text_field.substring(0,2);
            for (Map.Entry<String, Location> entry : map.entrySet()) {
                String key = entry.getKey();
                Location value = entry.getValue();
                if (key.substring(2,4).equals(dest_substring) && !value.getLoaded())
                { 		
                	 String pid = key;
                	 value.setLoaded(true);
                	 Task t = system.taskManager.createloadTask(pid,system.loadingDocks.get(0));
                     taskRows.add(new TaskRow(t.getTaskId(), t.getType().toString(), t.getParcelId(), t.getStatus().toString()));
                }
            }
            
            /*
            String pid ="NULL";
            for (String key : map.keySet())
            {
            	if (key.substring(2,4).equals("FR")) {
            		pid = key;
            		break;
            	}
            }
            if (pid.equals("NULL"))
            {
            	 system.getLogManager().logStorageWarning("No parcels to load");
                 return;
            }
            Task t = system.taskManager.createloadTask(pid,system.loadingDocks.get(0));
            taskRows.add(new TaskRow(t.getTaskId(), t.getType().toString(), t.getParcelId(), t.getStatus().toString()));*/
        });

        top.getChildren().addAll(btnStart, btnStop, btnCreateLoad,textField, btnCreateUnload, btnReloadLog);

        // center: robot table and task table
        robotTable.setPrefWidth(420);
        TableColumn<RobotRow, String> colId = new TableColumn<>("Robot"); colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<RobotRow, String> colStat = new TableColumn<>("Status"); colStat.setCellValueFactory(new PropertyValueFactory<>("status"));
        TableColumn<RobotRow, Double> colBattery = new TableColumn<>("Battery"); colBattery.setCellValueFactory(new PropertyValueFactory<>("battery"));
        robotTable.getColumns().addAll(colId, colStat, colBattery);
        robotTable.setItems(robotRows);

        taskTable.setPrefWidth(660);
        TableColumn<TaskRow, String> tId = new TableColumn<>("Task"); tId.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<TaskRow, String> tType = new TableColumn<>("Type"); tType.setCellValueFactory(new PropertyValueFactory<>("type"));
        TableColumn<TaskRow, String> tParcel = new TableColumn<>("Parcel"); tParcel.setCellValueFactory(new PropertyValueFactory<>("parcel"));
        TableColumn<TaskRow, String> tStatus = new TableColumn<>("Status"); tStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        taskTable.getColumns().addAll(tId, tType, tParcel, tStatus);
        taskTable.setItems(taskRows);

        HBox center = new HBox(12);
        center.setPadding(new Insets(8));
        center.getChildren().addAll(robotTable, taskTable);

        // bottom: visualization and logs + log controls
        VBox bottom = new VBox(8);
        bottom.setPadding(new Insets(8));

        // simple grid visualization of racks
        GridPane grid = new GridPane();
        grid.setHgap(6); grid.setVgap(6);
        int r = 0;
        for (StorageRack rack : system.storageManager.getRacks()) {
            VBox rackBox = new VBox(5);
            rackBox.setPadding(new Insets(5));
            rackBox.setStyle("-fx-border-color: gray; -fx-border-radius: 4; -fx-padding: 6;");
            rackBox.getChildren().add(new Label(rack.getId()));
            for (StorageSlot slot : rack.getSlots()) {
                Rectangle rect = new Rectangle(60, 18);
                rect.setArcWidth(6); rect.setArcHeight(6);
                rect.setFill(slot.isOccupied() ? Color.RED : Color.LIGHTGREEN);
                
                Label slot_label = new Label("");
                StackPane slotPane = new StackPane(rect, slot_label);
                //rackBox.getChildren().add(rect);
                rackBox.getChildren().add(slotPane);
                slotRectMap.put(slot, rect);//mychange for printing
                slotLabelMap.put(slot, slot_label);
            }
            grid.add(rackBox, r % 5, r / 5);
            r++;
        }

        // Log management UI
        HBox logControls = new HBox(8);
        logControls.setAlignment(Pos.CENTER_LEFT);

        TextField tfTarget = new TextField();
        tfTarget.setPromptText("Equipment name or filename (e.g. ROBOT-R-1 or SYSTEM or ROBOT-R-1_2025-10-26.log)");
        tfTarget.setPrefWidth(520);

        TextField tfDate = new TextField();
        tfDate.setPromptText("Date yyyy-MM-dd (optional)");
        tfDate.setPrefWidth(140);

        Button btnOpenByTarget = new Button("Open Log");
        Button btnDelete = new Button("Delete Log");
        Button btnMove = new Button("Move Log");
        Button btnArchive = new Button("Archive Selected");

        btnOpenByTarget.setOnAction(e -> {
            String target = tfTarget.getText().trim();
            String date = tfDate.getText().trim();
            if (target.isEmpty()) {
                system.getLogManager().logSystemWarning("Open Log: target empty");
                return;
            }
            Optional<String> content = system.getLogManager().readLog(target, date);
            Platform.runLater(() -> {
                logArea.clear();
                if (content.isPresent()) {
                    logArea.setText(content.get());
                } else {
                    logArea.setText("No matching log found for '" + target + "' on date '" + (date.isEmpty()?LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")):date) + "'");
                }
            });
        });

        btnDelete.setOnAction(e -> {
            String target = tfTarget.getText().trim();
            if (target.isEmpty()) { system.getLogManager().logSystemWarning("Delete Log: no filename provided"); return;}
            boolean ok = system.getLogManager().deleteLog(target);
            if (ok) {
                system.getLogManager().logSystem("User deleted log " + target);
            }
        });

        btnMove.setOnAction(e -> {
            String target = tfTarget.getText().trim();
            if (target.isEmpty()) { system.getLogManager().logSystemWarning("Move Log: no filename provided"); return;}
            boolean ok = system.getLogManager().moveLog(target);
            if (ok) {
                system.getLogManager().logSystem("User moved log " + target);
            }
        });

        btnArchive.setOnAction(e -> {
            // archive all logs that match the target regex or filename
            String target = tfTarget.getText().trim();
            if (target.isEmpty()) { system.getLogManager().logSystemWarning("Archive Logs: filter empty"); return;}
            List<Path> matches = system.getLogManager().listLogs("(?i)" + Pattern.quote(target));
            if (matches.isEmpty()) {
                system.getLogManager().logSystemWarning("Archive: no logs matched " + target);
                return;
            }
            List<String> fnames = new ArrayList<>();
            for (Path pth : matches) fnames.add(pth.getFileName().toString());
            Optional<Path> zip = system.getLogManager().archiveLogs(fnames, null);
            zip.ifPresent(p -> {
                Platform.runLater(() -> logArea.appendText("Archived to " + p.toString() + "\n"));
            });
        });

        logControls.getChildren().addAll(new Label("Log target/filename:"), tfTarget, tfDate, btnOpenByTarget, btnArchive, btnMove, btnDelete);

        logArea.setEditable(false);
        logArea.setPrefRowCount(10);

        bottom.getChildren().addAll(new Label("Warehouse Layout"), grid, new Label("Log Management"), logControls, new Label("Operation Log (selected file)"), logArea);

        root.setTop(top);
        root.setCenter(center);
        root.setBottom(bottom);
    }

    public void startUIUpdater() {
        uiTimeline = new Timeline(new KeyFrame(Duration.millis(700), e -> refreshUI()));
        uiTimeline.setCycleCount(Timeline.INDEFINITE);
        uiTimeline.play();
    }

    private void refreshUI() {
        // update robot table
        Platform.runLater(() -> {
            robotRows.clear();
            for (LogisticsRobot r : system.robots) {
                robotRows.add(new RobotRow(r.getId(), r.getStatus(), r.getBatteryLevel()));
            }
            // update tasks table: include queued tasks
            taskRows.clear();
            for (Task t : system.taskManager.getQueuedTasks()) {
                taskRows.add(new TaskRow(t.getTaskId(), t.getType().toString(), t.getParcelId(), t.getStatus().toString()));
            }
            // My change for updating racks
         // update warehouse grid rectangles
            //for (Map.Entry<StorageSlot, Rectangle> slot : slotRectMap.entrySet()) {
            for (StorageSlot slot : slotRectMap.keySet()) {
                Label slot_label = slotLabelMap.get(slot);
                Rectangle rect = slotRectMap.get(slot);
                rect.setFill(slot.isOccupied() ? Color.RED : Color.LIGHTGREEN);
                slot_label.setText(slot.isOccupied() ? slot.getParcel().getParcelId() : "");
            }
        });
    }
}

