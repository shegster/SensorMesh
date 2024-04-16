package com.projektarbeit.sensormesh.controller;

import com.projektarbeit.sensormesh.models.SensorFactory;
import com.projektarbeit.sensormesh.SensorViewFactory;
import com.projektarbeit.sensormesh.models.Sensor;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.layout.*;
import java.util.ArrayList;

public class SensorGridController {
    private final GridPane sensorGrid;
    private final ArrayList<Sensor> sensorsInGrid = new ArrayList<>();
    private final SensorViewFactory sensorViewFactory = new SensorViewFactory();

    public SensorGridController(GridPane sensorGrid){
        this.sensorGrid = sensorGrid;
        sensorsInGrid.addAll(SensorFactory.activeSensors);
    }

    public void updateGrid(){

        Platform.runLater(() -> {
            if(applyChanges(SensorFactory.activeSensors)){
                return;
            }

            for (Node view : sensorGrid.getChildren()){
                VBox vBox = (VBox) view;
                SensorControl<?> control = (SensorControl<?>) vBox.getUserData();
                if (control != null) {
                    control.update();
                } else {
                    System.out.println("kein controller erhalten");
                }

            }
        });
    }

    private void rebuildGrid(){

        //neue Maße für GridPane kalkulieren
        int rows = (int)Math.ceil(sensorsInGrid.size() / 2.0);
        System.out.println("calc rows: " + rows);
        int columns = 2;

        //GridPane mit neuen Maßen erzeugen
        sensorGrid.getChildren().clear();
        sensorGrid.addRow(rows);
        sensorGrid.addColumn(columns);
        sensorGrid.getRowConstraints().clear();
        sensorGrid.getColumnConstraints().clear();
        RowConstraints rowConstraints = new RowConstraints();
        rowConstraints.setVgrow(Priority.ALWAYS);
        rowConstraints.setPercentHeight(100.0/rows);
        for (int i = 0 ; i < rows; i++){
            sensorGrid.getRowConstraints().add(rowConstraints);
        }
        sensorGrid.getRowConstraints().add(rowConstraints);
        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setHgrow(Priority.ALWAYS);
        columnConstraints.setPercentWidth(100.0/columns);
        columnConstraints.setHalignment(HPos.CENTER);
        for (int i = 0 ; i < columns; i++){
            sensorGrid.getColumnConstraints().add(columnConstraints);
        }

        fillGrid();

        System.out.println("REBUILD!!!");
    }

    private void fillGrid(){
        //GridPane mit Inhalt füllen
        int sindex = 0;
        for (int rindex = 0; rindex<sensorGrid.getRowCount() && sindex < sensorsInGrid.size(); rindex++){
            for (int cindex = 0; cindex<sensorGrid.getColumnCount() && sindex < sensorsInGrid.size(); cindex++){

                VBox newElement = sensorViewFactory.getView(sensorsInGrid.get(sindex));
                sensorGrid.add(newElement,cindex,rindex);

                sindex++;
            }
        }
        System.out.println("FILL GRID!!!");
    }

    private boolean applyChanges(ArrayList<Sensor> active){
        System.out.println("Sensor anzahl: "+ sensorsInGrid.size());
        System.out.println("Subscribed anzahl: "+ active.size());

        return (addSensor(active) || removeSensor(active));
    }

    private boolean addSensor(ArrayList<Sensor> subscribed){
        //returns TRUE if grid rebuilt
        for (Sensor newSensor : subscribed){
            boolean contains = false;
            for (Sensor oldSensor : sensorsInGrid){
                if (oldSensor.getId() == newSensor.getId()) {
                    contains = true;
                    break;
                }
            }
            if(!contains){
                sensorsInGrid.add(newSensor);
                rebuildGrid();
                return true;
            }
        }
        return false;
    }

    private boolean removeSensor(ArrayList<Sensor> active){

        int removals = 0;

        for (int index = 0; index < sensorsInGrid.size(); index++) {
            boolean contains = false;
            for (Sensor sensor : active) {
                if (sensorsInGrid.get(index).getId() == sensor.getId()) {
                    contains = true;
                    break;
                }
            }
            if (!contains){
                sensorsInGrid.remove(index);
                removals++;
                index--;
            }
        }
        // Entferne alle markierten Nodes auf einmal.
        if(removals != 0){
            rebuildGrid();
            return true;
        }
       return false;
    }

}
