package de.brick6.nxtremote.logging;

public interface LogObserver {


  void logForAndroid(String message);

  void logForRobot(String message);
}
