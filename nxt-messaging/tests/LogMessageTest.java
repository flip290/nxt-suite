import static org.junit.Assert.assertTrue;

import messaging.comtoandroid.LogMessage;
import org.junit.Test;

public class LogMessageTest {

  @Test
  public void testLogMessageSysmmetrie() {
    String hallowelt = "Hallo Welt!";
    LogMessage message = new LogMessage(hallowelt);
    LogMessage fromBytes = new LogMessage(message.toNetworkArray());
    System.out.println(fromBytes.getMessage());
    assertTrue(hallowelt.equals(fromBytes.getMessage()));
  }


  /**
   * Should be >= 128.
   */
  @Test
  public void testForHugeSizes() {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < 128; i++) {
      builder.append(i + ",");
    }
    LogMessage message = new LogMessage(builder.toString());
    LogMessage fromBytes = new LogMessage(message.toNetworkArray());
    assertTrue(fromBytes.getMessage().length() <= 128);
  }
}
