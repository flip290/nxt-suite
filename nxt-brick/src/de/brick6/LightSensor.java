package de.brick6;

import lejos.nxt.SensorPort;

public final class LightSensor {
    private static lejos.nxt.LightSensor instance;

    private LightSensor() {
    }

    public static lejos.nxt.LightSensor get() {
        if (instance == null) {
            instance = new lejos.nxt.LightSensor(SensorPort.S2);
        }
        return instance;
    }
}
