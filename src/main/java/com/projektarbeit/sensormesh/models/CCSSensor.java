package com.projektarbeit.sensormesh.models;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.LinkedList;


public class CCSSensor extends Sensor {

    int eCO2;
    int tVoc;

    LinkedList<Integer> eco2List = new LinkedList<>();
    LinkedList<Integer> tVocList = new LinkedList<>();

    public CCSSensor(JsonNode data){
        super(data.get("name").asText(),data.get("id").asLong(), data.get("type").asText(), data.get("timestamp").asLong());

        this.eCO2 = data.get("eco2").asInt();
        this.tVoc = data.get("tvoc").asInt();
    }

    @Override
    public void updateData(JsonNode data) {
        resetSchedule();

        this.eCO2 = data.get("eco2").asInt();
        this.tVoc = data.get("tvoc").asInt();

        eco2List.add(eCO2);
        if(eco2List.size()>60) eco2List.pop();

        tVocList.add(tVoc);
        if (tVocList.size()>60) tVocList.pop();

        this.delayMillis = System.currentTimeMillis() - this.initialMillis;
        this.initialMillis = System.currentTimeMillis();


    }
    @Override
    public String getFXMLPath(){
        return "CCSView.fxml";
    }

    public int geteCO2() {
        return eCO2;
    }

    public int gettVoc() {
        return tVoc;
    }

}
