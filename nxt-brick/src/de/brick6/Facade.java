package de.brick6;

import messaging.command.Command;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.NXTConnection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

final class Facade extends Observable {
    private BluetoothState bluetoothState = BluetoothState.NOT_CONNECTED;
    private CommandHandler commandHandler;
    private lejos.nxt.comm.BTConnection bluetooth;
    private Thread thread;
    private static Facade instance;

    public static Facade getInstance() {
        if (instance == null) {
            instance = new Facade();
        }
        return instance;
    }

    private Facade() {

    }

    void setDriver(final Driver driver) {
        commandHandler = new CommandHandler(driver);
    }

    void connect() throws Exception {
        if (bluetoothState == BluetoothState.NOT_CONNECTED) {
            setState(BluetoothState.CONNECTING);
            bluetooth = Bluetooth.waitForConnection(0, NXTConnection.PACKET);
            System.out.println("connected");
            setState(BluetoothState.CONNECTED);
        } else {
            throw new Exception("still connected");
        }
    }

    void listen() throws Exception {
        if (bluetoothState != BluetoothState.CONNECTED) {
            throw new Exception("not connected");
        }
        thread = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    DataInputStream input = bluetooth.openDataInputStream();
                    Command command = null;
                    try {
                        byte[] array = new byte[Command.PARAM_BYTECOUNT + 1];
                        array[0] = input.readByte();
                        array[1] = input.readByte();
                        array[2] = input.readByte();
                        array[3] = input.readByte();
                        array[4] = input.readByte();
                        command = new Command(array);
                        System.out.println(command);
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                    }
                    try {
                        commandHandler.execute(command);
                    } catch (Exception e) {
                        Logger.get().log(e.getMessage());
                    }
                }
            }
        });
        thread.start();
    }

    void writeToOutputStream(final byte[] message) throws Exception {
        DataOutputStream stream = bluetooth.openDataOutputStream();
        stream.write(message);
        stream.flush();
    }

    private void setState(final BluetoothState state) {
        this.bluetoothState = state;
        switch (state) {
            case CONNECTED:
                notifyObservers("ConnectedEvent");
                break;
            case NOT_CONNECTED:
                notifyObservers("DisconnecedEvent");
                break;
            case CONNECTING:
                notifyObservers("ConnectingEvent");
                break;
            default:
                System.out.println("state not fully implemented");
        }
    }
}
