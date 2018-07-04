package de.brick6;

import lejos.nxt.SensorPort;

public final class UltraSonicSensor {
    private static lejos.nxt.UltrasonicSensor instance;

    private UltraSonicSensor() {
    }

    public static lejos.nxt.UltrasonicSensor get() {
        if (instance == null) {
            instance = new lejos.nxt.UltrasonicSensor(SensorPort.S1);
        }
        return instance;
    }
}
