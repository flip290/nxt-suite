package messaging.comtoandroid;

public class LogMessage {

  public static final int LOGMESSAGESIZE = 128;
  private String message;

  /**
   * Constructor.
   * @param message logmessage
   */
  public LogMessage(String message) {
    if (message == null) {
      message = "";
    }

    this.message = adjustMessageToCorrectSize(message);
  }

  /**
   * Construcotr from byte serialization.
   * @param message byte[]
   */
  public LogMessage(byte[] message) {
    //remove header where type is defined
    byte[] editedmessage = new byte[message.length - 1];
    System.arraycopy(message, 1, editedmessage, 0, message.length - 1);
    //trim trailing zeros
    editedmessage = LogUtility.trimFront(editedmessage);
    this.message = adjustMessageToCorrectSize(LogUtility.byteArrayToString(editedmessage));

  }

  /**
   * Serializes object into byte[] representation.
   * @return bytes[]
   */
  public byte[] toNetworkArray() {
    byte[] networkbytes = LogUtility.stringToByteArray(message);
    byte[] withtrailingzeros = new byte[LogMessage.LOGMESSAGESIZE + 1];
    System.arraycopy(networkbytes, 0, withtrailingzeros, LOGMESSAGESIZE - networkbytes.length + 1,
        networkbytes.length);
    withtrailingzeros[0] = (byte) DataTypeFromRobot.LOG_MESSAGE.ordinal();
    return withtrailingzeros;
  }

  private String adjustMessageToCorrectSize(String toshorten) {
    if (toshorten.length() > LOGMESSAGESIZE) {
      toshorten = toshorten.substring(0, LOGMESSAGESIZE);
    }
    return toshorten;
  }

  public String getMessage() {
    return message;
  }

}
