package com.projektarbeit.sensormesh.models;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public abstract class Sensor {

    protected long id;
    protected String name;
    protected long initialMillis;
    protected long delayMillis = 0;
    private final String type;
    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture scheduledFuture;

    Sensor(String name, long id,String type,long initialMillis){
        this.name = name;
        this.id = id;
        this.type = type;
        this.initialMillis = initialMillis;
        scheduleClose();
    }

    public abstract void updateData(JsonNode data);

    public abstract String getFXMLPath();

    private void scheduleClose(){
        //scheduler für Selbstterminination der Models
        scheduledFuture = scheduler.schedule(() -> {
            try {
                close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        },10, TimeUnit.SECONDS);
    }

    protected void resetSchedule(){
        //setzt Selbsttermination zurück
        if(scheduledFuture != null){
            scheduledFuture.cancel(false);
            scheduleClose();
        }
    }


    private void close(){
        SensorFactory.activeSensors.remove(this);
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public long getDelayMillis(){
        return delayMillis;
    }
}
