package com.projektarbeit.sensormesh.controller;

import com.projektarbeit.sensormesh.models.Sensor;

public abstract class SensorControl <T extends Sensor>{
    protected T model;

    public void setModel(T model) {
        this.model = model;
    }
    public abstract void update();

    public Object getModel() {
        return model;
    }
}
