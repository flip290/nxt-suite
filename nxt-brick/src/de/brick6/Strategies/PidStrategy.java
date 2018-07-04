package de.brick6.Strategies;

import de.brick6.LeftMotor;
import de.brick6.LightSensor;
import de.brick6.Strategy;
import de.brick6.UltraSonicSensor;
import de.brick6.RightMotor;
import messaging.strategies.Strategies;

public final class PidStrategy extends Strategy {
    private static final int EXPECTED_VALUE = 47;
    private static final int TOLERANCE = 5;
    private static final float KP = 20.0f;
    private static final float KD = 0.1f;
    private static final float KI = 0.25f;

    private static PidStrategy instance;
    private float integral = 0;
    private float derivative;
    private float perviouspError = 0;
    private float pError;
    private float correction;
    private float realSpeedLeft;
    private float realSpeedRight;

    float getRealSpeedLeft() {
        return realSpeedLeft;
    }

    float getRealSpeedRight() {
        return realSpeedRight;
    }

    public static PidStrategy get() {
        if (instance == null) {
            instance = new PidStrategy();
        }
        return instance;
    }


    public void reset() {
        correction = 0;
        integral = 0;
        derivative = 0;
        pError = 0;
        perviouspError = 0;
        setSpeed(BASE_SPEED);
    }

    int getExpectedValue() {
        return EXPECTED_VALUE;
    }

    public void run() {
        while (true) {
            if (isRunning()) {
                pError = (EXPECTED_VALUE - LightSensor.get().getLightValue());
                integral += KI * pError;
                derivative = (pError - perviouspError) * KD;
                correction = (KP * pError) + integral + derivative;

                realSpeedLeft = getSpeed() + correction;
                realSpeedRight = getSpeed() - correction;

                int distance = UltraSonicSensor.get().getDistance();
                if (distance <= 20) {
                    //notifyObservers("Overtake");
                    realSpeedLeft = realSpeedLeft * (distance / 20);
                    realSpeedRight = realSpeedRight * (distance / 20);
                }

                LeftMotor.get().setSpeed(realSpeedLeft);
                RightMotor.get().setSpeed(realSpeedRight);
                LeftMotor.get().forward();
                RightMotor.get().forward();

                perviouspError = pError;

                if (LightSensor.get().getLightValue() - EXPECTED_VALUE > TOLERANCE) {
                    //if (Math.abs(EXPECTED_VALUE - LightSensor.get().getLightValue()) > TOLERANCE) {
                    notifyObservers("LostLineEvent");
                }
            }
        }
    }

    public Strategies getEnum() {
        return Strategies.PID;
    }
}
