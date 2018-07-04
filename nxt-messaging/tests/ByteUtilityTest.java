import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

import messaging.ByteUtility;
import messaging.command.Command;
import messaging.command.CommandTypes;
import org.junit.Test;

public class ByteUtilityTest {


  @Test
  public void testBytesToInt() {
    byte[] bytes = {0x00, 0x00, 0x01, 0x2C};
    Command command = new Command(CommandTypes.DRIVE_BACKWARD, bytes);
    System.out.println("Command Param: " + command.getParam());
    assertTrue(command.getParam() == 300);
  }

  @Test
  public void testIntToBytes() {
    byte[] bytes = {0x00, 0x00, 0x01, 0x2C};
    byte[] output = ByteUtility.intToBytes(300);
    assertArrayEquals(bytes, output);
  }
}
