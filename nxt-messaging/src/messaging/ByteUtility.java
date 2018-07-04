package messaging;

import messaging.command.Command;

public class ByteUtility {


  /**
   * Converts network bluetooth data to int Bytes are interpreted as NetworkByteOrder, therefore Big
   * Endian.
   *
   * @param input the bytes to be converted in network byte order
   * @return integer representation of the input bytes
   */
  public static int bytesToInt(byte[] input) {

    int output = EndianTools.decodeintbe(input, 0);
    return output;
  }

  /**
   * Converts an integer to a byte representation The space to be allocated in the bytebuffer is.
   * determined by the convention of the number of bytes to be sent over the network which is
   * determined in a constant. -1 because we dont have the type here Bytes are interpreted as
   * NetworkByteOrder, therefore Big Endian
   */
  public static byte[] intToBytes(int input) {
    byte[] output = new byte[Command.PARAM_BYTECOUNT];
    EndianTools.encodeintbe(input, output, 0);
    return output;
  }


}
