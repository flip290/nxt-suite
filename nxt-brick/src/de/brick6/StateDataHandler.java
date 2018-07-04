package de.brick6;

import messaging.comtoandroid.StateData;

public final class StateDataHandler {

    private static StateDataHandler instance;
    private Facade bluetoothhandler;
    private Logger logger;

    public static StateDataHandler getInstance() {
        if (instance == null) {
            instance = new StateDataHandler();
        }
        return instance;
    }

    public StateDataHandler() {
        bluetoothhandler = Facade.getInstance();
        logger = Logger.get();
    }

    public void sendStateData(final StateData data) {
        try {
            bluetoothhandler.writeToOutputStream(data.toNetworkByteArray());
        } catch (Exception e) {
            logger.log(e.getMessage());
        }
    }
}
