package de.brick6.nxtremote.communication;

import android.bluetooth.BluetoothAdapter;
import de.brick6.nxtremote.logging.Logger;
import de.brick6.nxtremote.logging.RobotLogReader;
import de.brick6.nxtremote.ui.RobotStateChangeObserver;
import java.io.IOException;
import lejos.pc.comm.NXTComm;
import lejos.pc.comm.NXTCommException;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTInfo;
import messaging.Command;

/**
 * The Bluetoothhandler is implemented as as singleton. Handles the higher level bluetooth stuff.
 */
public final class BluetoothHandler extends BluetoothObservable {

  private static final String mac = "00:16:53:0E:71:DA";
  private static BluetoothHandler instance = null;
  private BluetoothState state = BluetoothState.NOT_CONNECTED;
  private CommAndroidToNxt nxtcomm;
  private Logger logger;
  private RobotStateChangeObserver observer;

  /**
   * Constructor.
   */
  private BluetoothHandler() {
    nxtcomm = new CommAndroidToNxt();
    logger = Logger.getInstance();
  }

  /**
   * Lazy getter.
   *
   * @return the instance of the class
   */
  public static BluetoothHandler getInstance() {
    if (instance == null) {
      instance = new BluetoothHandler();
    }
    return instance;
  }

  /**
   * Connects the device javato a nxt bot.
   *
   * @throws BluetoothException if something went wrong
   */
  public void connect() throws BluetoothException {
    checkBluetoothAvailability();
    NXTInfo nxtinfo = new NXTInfo(NXTCommFactory.BLUETOOTH, "bob", mac);
    try {
      nxtcomm.open(nxtinfo, NXTComm.PACKET);
      setState(BluetoothState.CONNECTED);
      startLogReader();
      logger.logForAndroid("Connected to " + nxtinfo.name + "//" + nxtinfo.deviceAddress);
    } catch (NXTCommException e) {
      throw new BluetoothException("Can't connect " + nxtinfo.name + "//" + nxtinfo.deviceAddress);
    }

  }

  /**
   * Disconnects from connected device.
   *
   * @throws BluetoothException if something went wrong
   */
  public void disconnect() throws BluetoothException {
    checkBluetoothAvailability();
    try {
      nxtcomm.close();
    } catch (IOException e) {
      throw new BluetoothException("Can't close connection");
    }
  }

  /**
   * Reads all available bytes from the nxtcomm.
   *
   * @return Byte array containig send data
   * @throws BluetoothException if something went wrong
   */
  public byte[] read() throws BluetoothException {
    byte[] readarray;
    try {
      readarray = nxtcomm.read();
    } catch (IOException e) {
      setState(BluetoothState.NOT_CONNECTED);
      throw new BluetoothException("Handler could not read from inputstream");
    }
    return readarray;
  }

  /**
   * Serializes a command object and sends it to the connected device.
   *
   * @param command the command to be sent
   * @throws BluetoothException if something went wrong
   */
  public void sendCommandOverBluetooth(Command command) throws BluetoothException {
    if (state != BluetoothState.CONNECTED) {
      throw new BluetoothException(
          "Command: " + command.getType().name() + "\n  Not connected, can't send anything");
    }
    logger.logForAndroid("Command to send:\n" + command.toString());
    command.toNetworkIntArray();
    try {
      nxtcomm.write(command.toNetworkByteArray());
    } catch (IOException e) {
      setState(BluetoothState.NOT_CONNECTED);
      throw new BluetoothException("Command can't be send");
    }
  }

  /**
   * Check Bluetooth availability.
   *
   * @throws BluetoothException if something went wrong
   */
  public void checkBluetoothAvailability() throws BluetoothException {
    BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

    if (adapter == null) {
      // Device does not support Bluetooth
      throw new BluetoothException("Device doesn't support bluetooth");
    } else {
      if (!adapter.isEnabled()) {
        // Bluetooth is not enable :)
        throw new BluetoothException("Bluetooth is not enabled");
      }
    }
  }

  /**
   * Returns state.
   *
   * @return the current bluetooth state.
   */
  public BluetoothState getState() {
    return state;
  }

  /**
   * Sets Bluetooth state and calls observer.
   *
   * @param state the bluetooth state
   */
  private void setState(BluetoothState state) {
    this.state = state;
    notifyObservers(state);
  }

  /**
   * Starts LogReader.
   */
  public void startLogReader() {
    if (state == BluetoothState.CONNECTED) {
      Thread logReader = new Thread(new RobotLogReader(observer));
      logReader.start();
      logger.logForAndroid("Started LogReader.");
    } else {
      logger.logForAndroid("Not connected, therefore can't start LogReader.");
    }

  }

  public void addLogReaderObserver(RobotStateChangeObserver observer) {
    this.observer = observer;
  }
}
