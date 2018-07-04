package de.brick6.nxtremote.communication;

import de.brick6.nxtremote.communication.BluetoothObservable;
import de.brick6.nxtremote.communication.BluetoothState;

/**
 * The AndroidObserver observes functionality to update ui stuff.
 */
public interface AndroidObserver {

  /**
   * Update is c alled by the observable after something happend.
   *
   * @param observable the observable
   * @param state the bluettoth state
   */
  void update(BluetoothObservable observable, BluetoothState state);
}
