package de.brick6;

import lejos.nxt.MotorPort;

public final class LeftMotor {
    private static lejos.nxt.NXTRegulatedMotor instance;

    private LeftMotor() {
    }

    public static lejos.nxt.NXTRegulatedMotor get() {
        if (instance == null) {
            instance = new lejos.nxt.NXTRegulatedMotor(MotorPort.A);
        }
        return instance;
    }
}
