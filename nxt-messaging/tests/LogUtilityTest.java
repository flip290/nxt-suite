import static org.junit.Assert.assertEquals;

import messaging.comtoandroid.LogUtility;
import org.junit.Test;

public class LogUtilityTest {

  @Test
  public void logUtility() {
    String input = "Hello World!";
    byte[] output = LogUtility.stringToByteArray(input);
    String result = LogUtility.byteArrayToString(output);
    assertEquals(result, input);
  }


}
