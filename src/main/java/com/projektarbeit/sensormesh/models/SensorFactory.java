package com.projektarbeit.sensormesh.models;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;

public class SensorFactory {
    public static ArrayList<Sensor> activeSensors = new ArrayList<>();


    public static void updateActive(@org.jetbrains.annotations.NotNull JsonNode data){
        long inputId = data.get("id").asLong();

        for (Sensor sensor : activeSensors){
            if (sensor.getId() == inputId){
                sensor.updateData(data);
                return;
            }
        }
        activeSensors.add(createSensor(data));
    }

    private static Sensor createSensor(JsonNode data){
        //spezielle Sub-Klassen nach Typ erstellen
        return switch (data.get("type").asText()) {
            case "CCS811" -> new CCSSensor(data);
            case "BMP280" -> new BMPSensor(data);
            default -> {
                System.out.println("Keinen passenden Typ gefunden!");
                yield null;
            }
        };
    }
}