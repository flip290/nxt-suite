package messaging;


/**
 * Tools for manipulating numbers in little-endian and big-endian encodings. Totally stolen from
 * lejos.util though. I am sorry
 */

public class EndianTools {

  /**
   * Decodes byte array as big endian.
   *
   * @param b to decode
   * @param off offset
   * @return int as result
   */
  public static int decodeintbe(byte[] b, int off) {
    return (b[off] << 24) | ((b[off + 1] & 0xFF) << 16)
        | ((b[off + 2] & 0xFF) << 8) | (b[off + 3] & 0xFF);
  }

  /**
   * Encodes integer as byte array in big endian order.
   *
   * @param v int to encode
   * @param b byte buffer
   * @param off offset for array
   */
  public static void encodeintbe(int v, byte[] b, int off) {
    b[off] = (byte) (v >>> 24);
    b[off + 1] = (byte) (v >>> 16);
    b[off + 2] = (byte) (v >>> 8);
    b[off + 3] = (byte) v;
  }


  /**
   * Encodes integer as byte array in little endian.
   *
   * @param v integer to encode
   * @param b byte buffer
   * @param off offset
   */
  public static void encodeintle(int v, byte[] b, int off) {
    b[off] = (byte) v;
    b[off + 1] = (byte) (v >>> 8);
    b[off + 2] = (byte) (v >>> 16);
    b[off + 3] = (byte) (v >>> 24);
  }


}
