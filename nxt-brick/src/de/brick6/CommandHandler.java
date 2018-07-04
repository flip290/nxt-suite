package de.brick6;

import messaging.command.Command;
import messaging.strategies.Strategies;
import de.brick6.Strategies.Manual;
import de.brick6.Strategies.PidStrategy;

class CommandHandler {
    private Driver driver;

    CommandHandler(final Driver driverValue) {
        driver = driverValue;
    }

    void execute(final Command command) throws Exception {
        driver.getStrategy().pause();
        int speed = driver.getStrategy().getSpeed();
        switch (command.getType()) {
            case DRIVE_FORWARD:
                LeftMotor.get().setSpeed(speed);
                RightMotor.get().setSpeed(speed);
                LeftMotor.get().forward();
                RightMotor.get().forward();
                break;
            case DRIVE_BACKWARD:
                LeftMotor.get().setSpeed(speed);
                RightMotor.get().setSpeed(speed);
                LeftMotor.get().backward();
                RightMotor.get().backward();
                break;
            case TURN_LEFT:
                LeftMotor.get().setSpeed(speed);
                RightMotor.get().setSpeed(speed);
                LeftMotor.get().backward();
                RightMotor.get().forward();
                break;
            case STOP_ROBOT:
                LeftMotor.get().setSpeed(0);
                RightMotor.get().setSpeed(0);
                break;
            case TURN_RIGHT:
                LeftMotor.get().setSpeed(speed);
                RightMotor.get().setSpeed(speed);
                LeftMotor.get().forward();
                RightMotor.get().backward();
                break;
            case START_FOLLOW_LINE_STRATEGY:
                driver.setStrategy(PidStrategy.get());
                driver.getStrategy().start();
                break;
            case SET_SPEED:
                speed = command.getParam();
                driver.getStrategy().setSpeed(speed);
                LeftMotor.get().setSpeed(speed);
                RightMotor.get().setSpeed(speed);
                break;
            case SWITCH_TO_PID_AUTO:
                if (command.getParam() == 0) {
                    Manual.get().setSwitchToPid(false);
                } else {
                    Manual.get().setSwitchToPid(true);
                }
                break;
            case SET_STRATEGY:
                switch (Strategies.values()[command.getParam()]) {
                    case MANUAL:
                        PidStrategy.get().reset();
                        LeftMotor.get().setSpeed(0);
                        RightMotor.get().setSpeed(0);
                        driver.setStrategy(Manual.get());
                        driver.getStrategy().start();
                        break;
                    case PID:
                        PidStrategy.get().reset();
                        driver.setStrategy(PidStrategy.get());
                        driver.getStrategy().start();
                        break;
                    default:
                        driver.getStrategy().resume();
                        throw new Exception("strategy not found");
                }
            default:
                driver.getStrategy().resume();
                throw new Exception("command not found: " + command.toString());
        }
        driver.getStrategy().resume();
    }
}
