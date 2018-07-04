package de.brick6.nxtremote.communication;

import java.util.ArrayList;

/**
 * Observable for Bluetoothclasses.
 */
public class BluetoothObservable {

  private ArrayList<AndroidObserver> observers = new ArrayList<>();

  /**
   * Adds an observer.
   *
   * @param observer the observer
   */
  public void addOberver(AndroidObserver observer) {
    observers.add(observer);
  }

  /**
   * Notifies all observers.
   *
   * @param state the changed bluetooth state
   */
  public void notifyObservers(final BluetoothState state) {
    for (AndroidObserver observer : observers) {
      observer.update(this, state);
    }
  }
}
