package de.brick6.nxtremote.communication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import de.brick6.nxtremote.logging.Logger;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Set;
import java.util.UUID;
import java.util.Vector;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import lejos.pc.comm.NXTComm;
import lejos.pc.comm.NXTCommException;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTCommInputStream;
import lejos.pc.comm.NXTCommOutputStream;
import lejos.pc.comm.NXTConnectionState;
import lejos.pc.comm.NXTInfo;

public class CommAndroidToNxt implements NXTComm {

  private static final UUID SERIAL_PORT_SERVICE_CLASS_UUID = UUID
      .fromString("00001101-0000-1000-8000-00805F9B34FB");
  private static Vector<BluetoothDevice> devices;
  private static Vector<NXTInfo> nxtInfos;
  private final String tag = "CommAndroidToNxt >>>>";
  private BluetoothAdapter mbtAdapter;
  private NXTInfo nxtInfo;
  private ConnectThread mconnectThread;
  private ReadThread mreadThread;
  private WriteThread mwriteThread;
  private LinkedBlockingQueue<byte[]> mreadQueue;
  private LinkedBlockingQueue<byte[]> mwriteQueue;
  private Logger logger;

  public CommAndroidToNxt() {
    logger = Logger.getInstance();
  }


  public int available() throws IOException {
    return 0;
  }

  private void cancelConnectThread() {
    if (mconnectThread != null) {
      mconnectThread.cancel();
      mconnectThread = null;
    }
  }

  private void cancelioThreads() {

    if (mreadThread != null) {
      mreadThread.cancel();
      mreadThread = null;
    }
    if (mwriteThread != null) {
      mwriteThread.cancel();
      mwriteThread = null;
    }
  }

  /**
   * Closes connection.
   *
   * @throws IOException if something went wrong.
   */
  public void close() throws IOException {
    Log.d(tag, "closing threads and socket");
    cancelioThreads();
    cancelConnectThread();

  }

  private byte[] concat(byte[] data1, byte[] data2) {
    int l1 = data1.length;
    int l2 = data2.length;

    byte[] data = new byte[l1 + l2];
    System.arraycopy(data1, 0, data, 0, l1);
    System.arraycopy(data2, 0, data, l1, l2);
    return data;
  }

  public InputStream getInputStream() {
    return new NXTCommInputStream(this);
  }

  public OutputStream getOutputStream() {
    return new NXTCommOutputStream(this);
  }

  public boolean open(NXTInfo nxt) throws NXTCommException {
    return open(nxt, PACKET);
  }

  /**
   * Opens connection.
   *
   * @param nxt which device
   * @param mode which mode.
   * @return has worked?
   * @throws NXTCommException if something went wrong
   */
  public boolean open(NXTInfo nxt, int mode) throws NXTCommException {
    if (mode == RAW) {
      throw new NXTCommException("RAW mode not implemented");
    }
    BluetoothDevice nxtDevice = null;
    SynchronousQueue<Boolean> connectQueue = new SynchronousQueue<Boolean>();
    if (mbtAdapter == null) {
      mbtAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    nxtDevice = mbtAdapter.getRemoteDevice(nxt.deviceAddress);

    try {

      mconnectThread = new ConnectThread(nxtDevice, connectQueue);
      mconnectThread.start();

      Boolean socketEstablished = connectQueue.take();//blocking call to wait for connection status
      Thread.yield();

      boolean socketConnected = socketEstablished;
      if (socketConnected) {
        nxt.connectionState = (mode == LCP ? NXTConnectionState.LCP_CONNECTED
            : NXTConnectionState.PACKET_STREAM_CONNECTED);
      } else {
        nxt.connectionState = NXTConnectionState.DISCONNECTED;
      }
      nxtInfo = nxt;

      return socketConnected;
    } catch (Exception e) {
      Log.e(tag, "ERROR in open: ", e);
      nxt.connectionState = NXTConnectionState.DISCONNECTED;
      throw new NXTCommException("ERROR in open: " + nxt.name + " failed: " + e.getMessage());
    }
  }

  /**
   * Will block until data is available.
   *
   * @return read data
   */
  public byte[] read() throws IOException {
    //Log.d(tag, "read called");
    byte[] b = null;

    while (b == null) {
      b = mreadQueue.poll();
      Thread.yield();
    }
    return b;
  }

  /**
   * search for device.
   *
   * @param name name of device
   * @param protocol which protocol
   * @return returns all found devices
   * @throws NXTCommException if something went wrong
   */
  public NXTInfo[] search(String name, int protocol) throws NXTCommException {
    //Log.d(tag, "search");
    nxtInfos = new Vector<NXTInfo>();
    devices = new Vector<BluetoothDevice>();
    mbtAdapter = BluetoothAdapter.getDefaultAdapter();
    // Get a set of currently paired devices
    Set<BluetoothDevice> pairedDevices = mbtAdapter.getBondedDevices();

    for (BluetoothDevice device : pairedDevices) {
      //Log.d(tag, "paired devices :" + device.getName() + "\n" + device.getAddress());

      if (device.getBluetoothClass().getMajorDeviceClass() == 2048) {
        devices.add(device);
      }
    }

    for (Enumeration<BluetoothDevice> enumd = devices.elements(); enumd.hasMoreElements(); ) {
      BluetoothDevice d = enumd.nextElement();
      Log.d(tag, "creating nxtInfo");
      nxtInfo = new NXTInfo();

      nxtInfo.name = d.getName();
      if (nxtInfo.name == null || nxtInfo.name.length() == 0) {
        nxtInfo.name = "Unknown";
      }
      nxtInfo.deviceAddress = d.getAddress();
      nxtInfo.protocol = NXTCommFactory.BLUETOOTH;

      if (name == null || name.equals(nxtInfo.name)) {

        //Log.d(tag, "adding " + d.getName());
        nxtInfos.addElement(nxtInfo);
      }
    }

    NXTInfo[] nxts = new NXTInfo[nxtInfos.size()];
    for (int i = 0; i < nxts.length; i++) {
      nxts[i] = nxtInfos.elementAt(i);

    }
    return nxts;
  }

  /**
   * Sends a request to the NXT brick.
   *
   * @param message Data to send.
   */
  public synchronized byte[] sendRequest(byte[] message, int replyLen) throws IOException {

    write(message);

    if (replyLen == 0) {
      return new byte[0];
    }

    byte[] b = read();

    if (b.length != replyLen) {
      throw new IOException("Unexpected reply length");
    }

    return b;
  }

  /**
   * Start io threads.
   *
   * @param socket on this socket
   * @param device and this device
   */
  public synchronized void startioThreads(BluetoothSocket socket, BluetoothDevice device) {

    cancelioThreads();

    mreadQueue = new LinkedBlockingQueue<byte[]>();
    mwriteQueue = new LinkedBlockingQueue<byte[]>();

    mwriteThread = new WriteThread(socket, mwriteQueue);
    mreadThread = new ReadThread(socket, mreadQueue);

    mwriteThread.start();
    mreadThread.start();
  }


  /**
   * Put data into write queue to be written by write thread. Will block if no space in queue. Queue
   * size is 2147483647, so this is not likely.
   *
   * @param data Data to send.
   */
  public void write(byte[] data) throws IOException {

    try {
      if (data != null) {
        mwriteQueue.put(data);
      }
      Thread.yield();
    } catch (InterruptedException e) {
      Log.e(tag, "write error ", e);
      e.printStackTrace();
    }

  }

  private static class ReadThread extends Thread {

    public InputStream is;
    String tag = "CommAndroidToNxt >>>>";
    boolean running = true;
    LinkedBlockingQueue<byte[]> mreadQueue;

    public ReadThread(BluetoothSocket socket, LinkedBlockingQueue<byte[]> mreadQueue) {
      try {
        is = socket.getInputStream();
        //Log.d(tag, "socket is connected to: " + socket.getRemoteDevice().getName());
        this.mreadQueue = mreadQueue;
      } catch (IOException e) {
        Log.e(tag, "ReadThread is error ", e);
      }
    }

    public void cancel() {
      running = false;
      mreadQueue.clear();
    }

    private byte[] read() {
      int lsb = -1;
      try {
        lsb = is.read();
      } catch (Exception e) {
        Log.e(tag, "read err lsb", e);
      }

      if (lsb < 0) {
        return null;
      }
      int msb = 0;

      try {
        msb = is.read();

      } catch (IOException e1) {
        Log.e(tag, "ReadThread read error msb", e1);
      }

      if (msb < 0) {
        return null;
      }
      int len = lsb | (msb << 8);
      byte[] bb = new byte[len];
      for (int i = 0; i < len; i++) {

        try {
          bb[i] = (byte) is.read();
        } catch (IOException e) {
          Log.e(tag, "ReadThread read error data", e);
        }
      }

      return bb;
    }


    @Override
    public void run() {
      setName("NCA read thread");
      byte[] tmpdata;
      while (running) {
        Thread.yield();
        tmpdata = null;

        tmpdata = read();

        if (tmpdata != null) {
          try {
            mreadQueue.put(tmpdata);
          } catch (InterruptedException e) {
            Log.e(tag, "ReadThread queue error ", e);
          }
        }
      }
    }

  }

  private class ConnectThread extends Thread {

    private final BluetoothDevice mmDevice;
    private BluetoothSocket mmSocket;
    private SynchronousQueue<Boolean> connectQueue;

    public ConnectThread(BluetoothDevice device, SynchronousQueue<Boolean> connectQueue) {
      mmDevice = device;
      BluetoothSocket tmp = null;
      this.connectQueue = connectQueue;
      try {
        tmp = device.createRfcommSocketToServiceRecord(SERIAL_PORT_SERVICE_CLASS_UUID);
      } catch (IOException e) {
        Log.e(tag, "create() failed", e);
      }
      mmSocket = tmp;
    }

    public void cancel() {
      try {
        mmSocket.close();
      } catch (IOException e) {
        logger.logForAndroid(e.getMessage());
        logger.logForAndroid("close() of connect socket failed");
      } finally {
        mmSocket = null;
      }
    }

    private void relayConnectionSuccess() {
      try { // notify calling thread that connection succeeded
        connectQueue.put(Boolean.TRUE);
      } catch (InterruptedException e) {
        logger.logForAndroid(e.getMessage());
      }
      //Log.d(tag, "Connection success -- is connected to " + mmDevice.getName());
      yield(); // allow main CommAndroidToNxt thread to read connected
      // status and finish NXTComm setup

    }

    private void relyConnectionFailure(IOException e) {
      try {
        // notify calling thread that connection failed
        connectQueue.put(Boolean.FALSE);
        Log.e(tag, "Connection failure -- unable to connect to socket ", e);
      } catch (InterruptedException e1) {
        logger.logForAndroid(e1.getMessage());
      }

      if (mmSocket != null) {
        cancel();
      }
    }

    @Override
    public void run() {

      setName("NCA ConnectThread");
      Log.i(tag, "BEGIN mconnectThread");
      // Make a connection to the BluetoothSocket
      // This is a blocking call and will only return on a
      // successful connection or an exception
      try {
        mmSocket.connect();
      } catch (IOException e) {
        relyConnectionFailure(e);
        return;
      }

      relayConnectionSuccess();
      startioThreads(mmSocket, mmDevice);
    }

  }

  private class WriteThread extends Thread {

    public OutputStream os;
    LinkedBlockingQueue<byte[]> mwriteQueue;
    private boolean running = true;

    public WriteThread(BluetoothSocket socket, LinkedBlockingQueue<byte[]> mwriteQueue) {
      try {
        os = socket.getOutputStream();
        this.mwriteQueue = mwriteQueue;
      } catch (IOException e) {
        Log.e(tag, "WriteThread OutputStream error ", e);
      }
    }

    public void cancel() {
      running = false;
      mreadQueue.clear();
    }

    @Override
    public void run() {
      setName("NCA - write thread");
      while (running) {
        try {
          byte[] data;
          data = mwriteQueue.take();
          write(data);
        } catch (InterruptedException e) {
          Log.e(tag, "WriteThread write error ", e);
        }
      }

    }

    void write(byte[] data) {
      byte[] lsbmsb = new byte[2];
      lsbmsb[0] = (byte) data.length;
      lsbmsb[1] = (byte) ((data.length >> 8) & 0xff);
      try {
        os.write(concat(lsbmsb, data));
        os.flush();
      } catch (IOException e) {
        Log.e(tag, "WriteThread write error ", e);
      }
    }
  }

}
