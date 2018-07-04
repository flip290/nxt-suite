package de.brick6.nxtremote.logging;

import android.util.Log;
import java.util.ArrayList;

public class Logger {

  private static Logger instance;
  private ArrayList<LogObserver> observers;

  public Logger() {
    observers = new ArrayList<>();
  }

  /**
   * Lazy getter for singleton logger.
   *
   * @return returns instance
   */
  public static Logger getInstance() {
    if (instance == null) {
      instance = new Logger();
    }
    return instance;
  }

  public void addObserver(LogObserver logObserver) {
    observers.add(logObserver);
    logForAndroid("Added Android LogView");
  }


  /**
   * Logs only to the log view for android.
   *
   * @param message message to be logged.
   */
  public void logForAndroid(final String message) {
    for (LogObserver observer : observers) {
      observer.logForAndroid(message);
    }
    Log.i("android", message);
  }

  /**
   * Logs only to the log view for the robot.
   *
   * @param message message to be logged.
   */
  public void logForRobot(String message) {
    for (LogObserver observer : observers) {
      observer.logForRobot(message);
    }
    Log.i("robot", message);
  }


  public void logForBoth(String message) {
    logForAndroid(message);
    logForRobot(message);
  }
}

