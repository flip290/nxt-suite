package de.brick6.nxtremote.ui;

import android.view.View;
import de.brick6.nxtremote.communication.BluetoothException;
import de.brick6.nxtremote.communication.BluetoothHandler;
import de.brick6.nxtremote.logging.Logger;

public class OnBluetoothConnectListener implements View.OnClickListener {

  private BluetoothHandler handler;
  private Logger logger;


  public OnBluetoothConnectListener() {
    handler = BluetoothHandler.getInstance();
    logger = Logger.getInstance();
  }

  @Override
  public void onClick(View v) {
    switch (handler.getState()) {
      case CONNECTED:
        try {
          handler.disconnect();
        } catch (BluetoothException e) {
          logger.logForAndroid(e.getMessage());
        }
        break;
      case CONNECTING:
        //nuthin
        break;
      case NOT_CONNECTED:
        try {
          handler.connect();
        } catch (BluetoothException e) {
          logger.logForAndroid(e.getMessage());
        }
        break;
      default:
        logger.logForAndroid("Unknown BluetoothState in: " + getClass().getSimpleName());
        break;
    }
  }
}
