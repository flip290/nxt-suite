package de.brick6.nxtremote.ui;

import messaging.StateData;

public interface RobotStateChangeObserver {

  void applyNewState(StateData data);
}
