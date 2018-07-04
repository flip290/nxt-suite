package messaging.command;

import messaging.ByteUtility;
import messaging.EndianTools;

public class Command {

  //Param Bytecount is the number of bytes for the parameter section
  public static final int PARAM_BYTECOUNT = 4;
  public static final int SIZE = 2;
  private CommandTypes type;
  private int param;


  /**
   * Constructor for raw, unconverted bluetooth data.
   *
   * @param bytes Chunk of PARAM_BTECOUNT+1 bytes
   */
  public Command(byte[] bytes) {
    //split array in first byte (indicates type) and the rest
    // which will be interpreted as parameter of command
    Byte typeByte = bytes[0];
    //create instance of enum using the ordinal (which is the int representation)
    this.type = CommandTypes.values()[typeByte.intValue()];
    byte[] param = new byte[Command.PARAM_BYTECOUNT];
    System.arraycopy(bytes, 1, param, 0, Command.PARAM_BYTECOUNT);
    setParam(param);
  }

  public Command(int[] raw) {
    type = CommandTypes.values()[raw[0] - 1];
    param = raw[1];
  }

  public Command(CommandTypes type, int param) {
    this.type = type;
    this.param = param;
  }

  public Command(CommandTypes type) {
    this.type = type;
    this.param = 0;
  }


  public Command(CommandTypes type, byte[] input) {
    this.type = type;
    setParam(input);
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
    return (int) Math.pow(type.ordinal() + 100, param);
  }

  /**
   * Returns the object as a byte array in NetworkByte Order, which can be used in the constructor.
   */
  public byte[] toNetworkByteArray() {
    byte[] output = new byte[Command.PARAM_BYTECOUNT + 1];
    //encode type as byte - little hack to encode as
    // littleendian so that the byte is on the first spot of the array
    EndianTools.encodeintle(this.type.ordinal(), output, 0);
    //encode param as byte with an offset of 1 to
    // overwrite the last part of the byte from first encoding
    EndianTools.encodeintbe(this.param, output, 1);
    return output;
  }

  /**
   * Serializes Command into NetworkArray of type int.
   * @return the int[] representation
   */
  public int[] toNetworkIntArray() {
    int[] output = new int[Command.SIZE];
    output[0] = type.ordinal() + 1;
    output[1] = param;
    return output;
  }

  public String toString() {
    return "Command:\nType: " + type.name() + "\nParameter: " + param;
  }

  public CommandTypes getType() {
    return type;
  }

  public int getParam() {
    return param;
  }

  public void setParam(int param) {
    this.param = param;
  }

  public void setParam(byte[] param) {
    this.param = ByteUtility.bytesToInt(param);
  }


}
