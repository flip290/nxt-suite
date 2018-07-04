package de.brick6;

import messaging.comtoandroid.StateData;
import messaging.comtoandroid.StateTypes;
import messaging.strategies.Strategies;

public abstract class Strategy extends Observable implements Runnable {
    protected static final int BASE_SPEED = 300;
    private boolean running;
    private Thread thread;
    private int speed = BASE_SPEED;

    protected final int getSpeed() {
        return speed;
    }

    protected final void setSpeed(final int value) {
        speed = value;
        StateDataHandler.getInstance().sendStateData(new StateData(StateTypes.SPEED, speed));

    }

    protected final boolean isRunning() {
        return running;
    }

    public abstract void run();

    final void start() {
        running = true;
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
        }
    }

    final void pause() {
        running = false;
    }

    final void resume() {
        running = true;
    }

    public abstract Strategies getEnum();
}
