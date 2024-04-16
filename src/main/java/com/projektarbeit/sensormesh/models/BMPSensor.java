package com.projektarbeit.sensormesh.models;

import com.fasterxml.jackson.databind.JsonNode;


public class BMPSensor extends Sensor{

    double temp;
    double pres;

    public BMPSensor(JsonNode data){
        super(data.get("name").asText(), data.get("id").asLong(), data.get("type").asText(), data.get("timestamp").asLong());

        this.temp = data.get("temp").asDouble(-999999);
        this.pres = data.get("pres").asDouble(-999999);
    }


    @Override
    public void updateData(JsonNode data) {
        resetSchedule();

        this.temp = data.get("temp").asDouble();
        this.pres = data.get("pres").asDouble();

        this.delayMillis = System.currentTimeMillis() - this.initialMillis;
        this.initialMillis = System.currentTimeMillis();

    }

    @Override
    public String getFXMLPath(){
        return "BMPView.fxml";
    }

    public double getTemp() {return temp;}

    public double getPres() {return pres;}
}
