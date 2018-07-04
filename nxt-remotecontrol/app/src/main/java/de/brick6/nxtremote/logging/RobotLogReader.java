package de.brick6.nxtremote.logging;

import de.brick6.nxtremote.communication.BluetoothException;
import de.brick6.nxtremote.communication.BluetoothHandler;
import de.brick6.nxtremote.ui.RobotStateChangeObserver;
import messaging.DataTypeFromRobot;
import messaging.LogMessage;
import messaging.StateData;

/**
 * Reads log sent from Robot. Is Ruannable.
 */
public final class RobotLogReader implements Runnable {

  private BluetoothHandler handler;
  private Logger logger;
  private RobotStateChangeObserver observer;
  private long bytecount;

  public RobotLogReader(RobotStateChangeObserver observer) {
    this.observer = observer;
  }

  /**
   * Setup class.
   */
  public void setup() {
    handler = BluetoothHandler.getInstance();
    logger = Logger.getInstance();
  }

  /**
   * Overwritten run method.
   */
  @Override
  public void run() {
    setup();
    logger.logForBoth("RobotLogReader ist set up");

    while (true) {
      try {
        byte[] bytes = handler.read();
        bytecount += bytes.length;
        logger.logForAndroid("Total amounts of bytes read: "+bytecount);
        DataTypeFromRobot type = DataTypeFromRobot.values()[bytes[0]];
        switch (type) {
          case LOG_MESSAGE:
            handleLog(bytes);
            break;
          case STATEDATA:
            handleStateData(bytes);
            break;
          default:
            logger.logForAndroid("Unexpected DataType");
        }

      } catch (BluetoothException e) {
        logger.logForAndroid(e.getMessage());
      }
    }
  }

  private void handleLog(byte[] log) {
    LogMessage message = new LogMessage(log);
    logger.logForRobot(message.getMessage());
  }

  private void handleStateData(byte[] statedata) {
    StateData stateData = new StateData(statedata);
    observer.applyNewState(stateData);
  }

}
