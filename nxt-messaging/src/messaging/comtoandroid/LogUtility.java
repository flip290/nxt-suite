package messaging.comtoandroid;

import java.io.UnsupportedEncodingException;

public class LogUtility {

  /**
   * Converts a byte Array to a string in ascii encoidng.
   *
   * @param message bytes to be converted
   * @return the message as a string
   */
  public static String byteArrayToString(byte[] message) {
    try {
      return new String(message, "US-ASCII");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return "";
  }

  /**
   * Converts a message to a byte array of ascii encoding.
   *
   * @param message message to convert
   * @return ascii byte array
   */
  public static byte[] stringToByteArray(String message) {
    byte[] b = new byte[message.length()];

    for (int i = 0; i < b.length; i++) {
      b[i] = (byte) message.charAt(i);
    }

    return b;

  }

  /**
   * Trims all 0 bytes out of a bytearray.
   * Searches for the start of the "real" payload. Then returns just that.
   *
   * @param message to be trimmed.
   * @return trimmed message
   */
  public static byte[] trimFront(byte[] message) {
    int startOfData = 0;
    for (int i = 0; i < message.length; i++) {
      if (message[i] != 0) {
        startOfData = i;
        break;
      }
    }
    int realPayloadLenght = message.length - startOfData;
    byte[] trimmed = new byte[realPayloadLenght];
    System.arraycopy(message, startOfData, trimmed, 0, realPayloadLenght);
    return trimmed;
  }
}
