package de.brick6.nxtremote.communication;

import android.util.Log;

/**
 * Custom exception for dealing with bluetooth issues.
 */
public class BluetoothException extends Exception {

  /**
   * Constructor.
   *
   * @param message a message for the exception
   */
  public BluetoothException(final String message) {
    super(message);
    log(message);
  }

  /**
   * Logs the message.
   *
   * @param message message to log
   */
  private void log(final String message) {
    Log.e("bluetooth", "Err:" + message);
  }
}
