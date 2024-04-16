package com.projektarbeit.sensormesh;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fazecast.jSerialComm.SerialPort;
import com.projektarbeit.sensormesh.models.RootNode;
import com.projektarbeit.sensormesh.models.SensorFactory;

public class SerialInputReceiver extends  Thread{

    @Override
    public void run(){
        //AP-Node Root immer über COM-Port 5 anschließen
        SerialPort comPort = SerialPort.getCommPort("COM5");

        if(comPort != null){
            try {
                //Serielle Verbindung zu AP-Node aufbauen
                comPort.setBaudRate(115200);
                comPort.openPort();
                System.out.println("Port opened: " + comPort.getSystemPortName());

                StringBuilder messageBuilder = new StringBuilder();
                ObjectMapper objectMapper = new ObjectMapper();

                while (true){
                    //warten auf Input
                    while (comPort.bytesAvailable() <= 0){
                        Thread.sleep(20);
                    }
                    //buffer gemäß der benötigten Bytes erstellen und beschreiben
                    byte[] readBuffer = new byte[comPort.bytesAvailable()];
                    comPort.readBytes(readBuffer, readBuffer.length);
                    //geleseene Daten zu String konvertieren und dem StringBuilder hinzufügen
                    messageBuilder.append(new String(readBuffer));

                    //JSON mittels delimiter (\n) isolieren deseriallisieren und entsprechend weiterleiten
                    int delimiterIndex;

                    while ((delimiterIndex = messageBuilder.indexOf("\n")) != -1){
                        String jsonString = messageBuilder.substring(0,delimiterIndex).trim();
                        if(!jsonString.isEmpty()){
                            if(jsonString.startsWith("{") && jsonString.endsWith("}")) {
                                try {
                                    JsonNode data = objectMapper.readTree(jsonString);
                                    if(data.get("type").asText().equals("ROOT")){
                                        RootNode.update(data);
                                    } else {
                                        SensorFactory.updateActive(data);
                                    }
                                } catch (JsonProcessingException e) {
                                    throw new RuntimeException(e);
                                }
                            }  else {
                                System.out.println(jsonString);
                            }
                        }
                        messageBuilder.delete(0 , delimiterIndex+1);
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } else {
            System.out.println("ESP32 COM not found!");
        }
    }
}
