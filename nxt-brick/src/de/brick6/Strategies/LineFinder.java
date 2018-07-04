package de.brick6.Strategies;

import de.brick6.LeftMotor;
import de.brick6.LightSensor;
import de.brick6.RightMotor;
import de.brick6.Strategy;
import messaging.strategies.Strategies;

public final class LineFinder extends Strategy {
    private static LineFinder instance;
    private static final int TOLERANCE = 7;
    private boolean direction = true;

    public static LineFinder get() {
        if (instance == null) {
            instance = new LineFinder();
        }
        return instance;
    }

    @Override
    public void run() {
        while (true) {
            if (isRunning()) {
                int tries = 1;

                if (direction) {
                    lookLeft(tries);
                } else {
                    lookRight(tries);
                }

                if (Math.abs(PidStrategy.get().getExpectedValue() - LightSensor.get().getLightValue()) < TOLERANCE) {
                    notifyObservers("FoundLineEvent");
                }

                tries++;
                direction = !direction;
            }
        }
    }

    public Strategies getEnum() {
        return Strategies.MANUAL;
    }

    private void lookRight(final int tries) {
        LeftMotor.get().setSpeed(100 * tries);
        RightMotor.get().setSpeed(100 * tries);
        LeftMotor.get().forward();
        RightMotor.get().backward();
    }

    private void lookLeft(final int tries) {
        LeftMotor.get().setSpeed(100 * tries);
        RightMotor.get().setSpeed(100 * tries);
        LeftMotor.get().backward();
        RightMotor.get().forward();
    }
}
