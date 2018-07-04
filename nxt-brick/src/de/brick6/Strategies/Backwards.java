package de.brick6.Strategies;

import de.brick6.LeftMotor;
import de.brick6.LightSensor;
import de.brick6.RightMotor;
import de.brick6.Strategy;
import messaging.strategies.Strategies;

public final class Backwards extends Strategy {
    private static final int TOLERANCE = 7;
    private static Backwards instance;

    public static Backwards get() {
        if (instance == null) {
            instance = new Backwards();
        }
        return instance;
    }

    public Strategies getEnum() {
        return Strategies.MANUAL;
    }

    @Override
    public void run() {
        while (true) {
            if (isRunning()) {
                LeftMotor.get().setSpeed(100);
                RightMotor.get().setSpeed(100);
                LeftMotor.get().backward();
                RightMotor.get().backward();

                if (Math.abs(PidStrategy.get().getExpectedValue() - LightSensor.get().getLightValue()) < TOLERANCE) {
                    notifyObservers("FoundLineEvent");
                }
            }
        }
    }
}
