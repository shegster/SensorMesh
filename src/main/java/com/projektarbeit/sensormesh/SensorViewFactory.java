package com.projektarbeit.sensormesh;

import com.projektarbeit.sensormesh.controller.BMPController;
import com.projektarbeit.sensormesh.controller.CCSController;
import com.projektarbeit.sensormesh.models.BMPSensor;
import com.projektarbeit.sensormesh.models.CCSSensor;
import com.projektarbeit.sensormesh.models.Sensor;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class SensorViewFactory {
    public <T extends Sensor> VBox getView (T sensor){

        try {
            //FXML-Datei mittels Path aus Model-Instanz laden
            FXMLLoader loader = new FXMLLoader(getClass().getResource(sensor.getFXMLPath()));
            VBox view = (VBox) loader.load();

            Object controller = loader.getController();
            //Prüfung der Generics und laden des übergebenen Models in den Controller der erzeugten View
            if(controller instanceof CCSController && sensor instanceof CCSSensor){
                ((CCSController) controller).setModel((CCSSensor)sensor);
                view.setUserData(controller);
            } else if(controller instanceof BMPController && sensor instanceof BMPSensor){
                ((BMPController) controller).setModel((BMPSensor) sensor);
                view.setUserData(controller);
            }
            return view;
        } catch (IOException e){
            e.printStackTrace();
            return new VBox(new Label("Keine View Vorhanden"));
        }

    }
}
