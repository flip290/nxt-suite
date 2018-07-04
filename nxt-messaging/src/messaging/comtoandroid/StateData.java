package messaging.comtoandroid;

import messaging.ByteUtility;
import messaging.EndianTools;
import messaging.command.Command;

public class StateData {

  public static final int PARAM_BYTECOUNT = 4;
  public static final int SIZE = 2;
  private StateTypes type;
  private int data;


  /**
   * Constructor form byte[] representation.
   *
   * @param bytes from network
   */
  public StateData(byte[] bytes) {
    byte[] editedmessage = new byte[bytes.length - 1];
    System.arraycopy(bytes, 1, editedmessage, 0, bytes.length - 1);
    //split array in first byte (indicates type) and the rest
    // which will be interpreted as parameter of command
    Byte typeByte = editedmessage[0];
    //create instance of enum using the ordinal (which is the int representation)
    this.type = StateTypes.values()[typeByte.intValue()];
    byte[] param = new byte[Command.PARAM_BYTECOUNT];
    System.arraycopy(editedmessage, 1, param, 0, Command.PARAM_BYTECOUNT);
    setParam(param);
  }

  /**
   * Constructor.
   *
   * @param type field
   * @param data field
   */
  public StateData(StateTypes type, int data) {
    this.type = type;
    this.data = data;
  }

  /**
   * Serializes object to networkarray.
   *
   * @return byte[] representation.
   */
  public byte[] toNetworkByteArray() {
    byte[] output = new byte[StateData.PARAM_BYTECOUNT + 2];
    output[0] = (byte) DataTypeFromRobot.STATEDATA.ordinal();
    //encode type as byte - little hack to encode as
    // littleendian so that the byte is on the first spot of the array
    EndianTools.encodeintle(this.type.ordinal(), output, 1);
    //encode param as byte with an offset of 1 to
    // overwrite the last part of the byte from first encoding
    EndianTools.encodeintbe(this.data, output, 2);
    return output;
  }

  /**
   * Setter for param.
   *
   * @param data byte[] as param
   */
  public void setParam(byte[] data) {
    this.data = ByteUtility.bytesToInt(data);
  }

  /**
   * Printable String.
   */
  public String toString() {
    return "StateData:\n  Type: " + type.name() + "\nParameter: " + data;
  }

  /**
   * Getter.
   *
   * @return datafield.
   */
  public StateTypes getType() {
    return type;
  }

  /**
   * Getter.
   *
   * @return datafield
   */
  public int getData() {
    return data;


  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    return obj.hashCode() == this.hashCode();
  }

  @Override
  public int hashCode() {
    return (int) Math.pow(type.ordinal() + 100, data);
  }

}

