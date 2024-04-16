module com.projektarbeit.sensormesh {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;
    requires com.fazecast.jSerialComm;
    requires annotations;


    opens com.projektarbeit.sensormesh to javafx.fxml, javafx.graphics;
    exports com.projektarbeit.sensormesh;
    exports com.projektarbeit.sensormesh.controller;
    opens com.projektarbeit.sensormesh.controller to javafx.fxml;
    exports com.projektarbeit.sensormesh.models;
    opens com.projektarbeit.sensormesh.models to javafx.fxml, javafx.graphics;
}