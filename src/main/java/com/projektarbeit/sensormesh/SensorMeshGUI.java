package com.projektarbeit.sensormesh;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SensorMeshGUI extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(SensorMeshGUI.class.getResource("MainView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 900, 600);
        stage.setTitle("SensorMeshGUI");
        stage.setScene(scene);
        stage.setResizable(true);
        stage.setMaximized(true);

        SerialInputReceiver serialInputReceiver = new SerialInputReceiver();
        serialInputReceiver.start();

        stage.setOnCloseRequest(event -> {
            Platform.exit();  // Beende die JavaFX-Anwendung
            System.exit(0);   // Beende die JVM
        });

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}