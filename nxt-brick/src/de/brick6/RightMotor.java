package de.brick6;

import lejos.nxt.MotorPort;

public final class RightMotor {
    private static lejos.nxt.NXTRegulatedMotor instance;

    private RightMotor() {
    }

    public static lejos.nxt.NXTRegulatedMotor get() {
        if (instance == null) {
            instance = new lejos.nxt.NXTRegulatedMotor(MotorPort.B);
        }
        return instance;
    }
}
