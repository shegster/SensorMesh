package com.projektarbeit.sensormesh.controller;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import com.projektarbeit.sensormesh.models.RootNode;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

public class mainViewController {

    @FXML
    private Label meshName;
    @FXML
    private ListView<String> nodeList;
    @FXML
    private GridPane sensorGrid;
    @FXML
    private Label time;

    SensorGridController gridController;
    ObservableList<String> nodeItems = FXCollections.observableArrayList(
            "Willkommen",
        "auf",
        "der",
        "Oberfläche"
    );

    @FXML
    void initialize() {
        //Initialisierung der Oberfläche
        time.setText(new Date().toString());
        meshName.setText("Warte auf Verbindung...");

        //GridPane 1x1 setzten
        sensorGrid.getRowConstraints().clear();
        sensorGrid.getColumnConstraints().clear();
        RowConstraints row = new RowConstraints();
        sensorGrid.getRowConstraints().add(row);
        ColumnConstraints column = new ColumnConstraints();
        sensorGrid.getColumnConstraints().add(column);
        //größe des Grids einstellen
        sensorGrid.boundsInParentProperty().addListener((observable, oldBounds, newBounds) -> {
            sensorGrid.setMaxWidth(newBounds.getWidth());
            sensorGrid.setMaxHeight(newBounds.getHeight());
        });

        gridController = new SensorGridController(sensorGrid);

        nodeList.setItems(nodeItems);

        nodeList.setCellFactory(param -> new ListCell<String>(){
            @Override
            protected void updateItem(String node, boolean empty){
                //Vlist für Node-Übersicht konfigurieren
                super.updateItem(node,empty);

                if(empty || node == null){
                    setText(null);
                    setStyle("-fx-background-color: transparent;");
                } else{
                    setText(node);
                    setAlignment(Pos.CENTER);
                    setStyle("-fx-background-color: transparent; -fx-font-weight: bold;");
                }
            }
        });

        periodicUpdate();
    }

    @FXML
    void update(){
        //Update-Befehl Thread sicher aufgerufen
        Platform.runLater(()->{
            this.time.setText(new Date().toString());
            this.meshName.setText(RootNode.getName());
            gridController.updateGrid();
            nodeList.getItems().clear();
            for (long nodeId : RootNode.getNodeList()){
                nodeList.getItems().add(Long.toString(nodeId));
            }
        });
    }

    void periodicUpdate(){
        //periodisches Triggern des Update-Befehls
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            update();;
        },5000,1000, TimeUnit.MILLISECONDS);
    }

}