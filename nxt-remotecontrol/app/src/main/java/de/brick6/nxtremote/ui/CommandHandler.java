package de.brick6.nxtremote.ui;

import de.brick6.nxtremote.communication.BluetoothException;
import de.brick6.nxtremote.communication.BluetoothHandler;
import de.brick6.nxtremote.communication.BluetoothState;
import de.brick6.nxtremote.logging.Logger;
import messaging.Command;
import messaging.CommandTypes;

public class CommandHandler {

  private Logger logger;
  private BluetoothHandler handler;

  /**
   * Constructor.
   */
  public CommandHandler() {
    logger = Logger.getInstance();
    handler = BluetoothHandler.getInstance();
  }

  //onClickListener

  /**
   * Send Command.
   */
  public void sendCommand(Command command) {

    try {
      handler.sendCommandOverBluetooth(command);
    } catch (BluetoothException e) {
      logger.logForAndroid(e.getMessage());
    }
  }

  public void sendDefaultCommand(CommandTypes type) {
    sendCommand(new Command(type));
  }

}
