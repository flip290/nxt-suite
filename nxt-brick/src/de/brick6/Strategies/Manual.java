package de.brick6.Strategies;

import de.brick6.LeftMotor;
import de.brick6.LightSensor;
import de.brick6.RightMotor;
import de.brick6.Strategy;
import messaging.strategies.Strategies;

public final class Manual extends Strategy {
    private static final int TOLERANCE = 7;
    private static Manual instance;
    private boolean switchToPid = false;

    public void setSwitchToPid(final boolean value) {
        switchToPid = value;
    }

    public static Manual get() {
        if (instance == null) {
            instance = new Manual();
        }
        return instance;
    }

    public Strategies getEnum() {
        return Strategies.MANUAL;
    }

    public void run() {
        LeftMotor.get().setSpeed(0);
        RightMotor.get().setSpeed(0);

        while (true) {
            if (isRunning()) {
                if (switchToPid) {
                    if (Math.abs(PidStrategy.get().getExpectedValue() - LightSensor.get().getLightValue()) < TOLERANCE) {
                        notifyObservers("FoundLineEvent");
                    }
                }
            }
        }
    }
}
