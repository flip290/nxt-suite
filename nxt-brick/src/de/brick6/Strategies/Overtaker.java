package de.brick6.Strategies;


import de.brick6.LeftMotor;
import de.brick6.LightSensor;
import de.brick6.RightMotor;
import de.brick6.Strategy;
import de.brick6.Logger;
import messaging.strategies.Strategies;

public final class Overtaker extends Strategy {
    private static final int TOLERANCE = 7;
    private static final int RADIUS = 150;
    private float lSpeed = PidStrategy.get().getRealSpeedLeft(); //lSpeed; // = PidStrategy.getLSpeed();
    private float rSpeed = PidStrategy.get().getRealSpeedRight(); // = PidStrategy.getRSpeed();
    private boolean direction = true;
    private int correction = 0;
    private static Overtaker instance;

    public static Overtaker get() {

        if (instance == null) {
            instance = new Overtaker();
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

                correction += 50;
                if (lSpeed > rSpeed) {
                    if (direction) {
                        LeftMotor.get().setSpeed(lSpeed + correction);
                        RightMotor.get().setSpeed(rSpeed - correction);
                    } else {
                        LeftMotor.get().setSpeed(lSpeed - correction);
                        RightMotor.get().setSpeed(rSpeed + correction);
                    }
                } else {
                    if (direction) {
                        LeftMotor.get().setSpeed(lSpeed - correction);
                        RightMotor.get().setSpeed(rSpeed + correction);
                    } else {
                        LeftMotor.get().setSpeed(lSpeed + correction);
                        RightMotor.get().setSpeed(rSpeed - correction);
                    }
                }
                LeftMotor.get().forward();
                RightMotor.get().forward();

                if (correction >= RADIUS) {
                    direction = false;
                    if (lSpeed > rSpeed) {
                        lSpeed += correction;
                        rSpeed -= correction;
                    } else {
                        lSpeed -= correction;
                        rSpeed += correction;
                    }
                    correction = 0;
                }

                if (!direction && Math.abs(PidStrategy.get().getExpectedValue() - LightSensor.get().getLightValue()) < TOLERANCE) {
                    //Logger.get().log(Integer.toString(diff));
                    notifyObservers("FoundLineEvent");
                }
            }
        }
    }
}
