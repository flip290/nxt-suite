package de.brick6;

import messaging.comtoandroid.LogMessage;

public final class Logger {
    private static Logger instance;

    public static Logger get() {
        if (instance == null) {
            instance = new Logger();
        }
        return instance;
    }


    private void logToLCD(final String message) {
        System.out.println(message);
    }

    private void logOverBluetooth(final String message) {
        LogMessage logMessage = new LogMessage(message);
        try {
            Facade.getInstance().writeToOutputStream(logMessage.toNetworkArray());
        } catch (Exception e) {
            logToLCD(e.getMessage());
        }
    }

    public void log(final String message) {
        logToLCD(message);
        logOverBluetooth(message);
    }
}
