import static org.junit.Assert.assertTrue;

import java.util.Random;

import messaging.command.Command;
import messaging.command.CommandTypes;
import org.junit.Test;

public class CommandTest {


  @Test
  public void testByteArrayConstructor() {
    byte[] bytes = {(byte) CommandTypes.DRIVE_BACKWARD.ordinal(), 0x00, 0x00, 0x01, 0x2C};
    Command fromBytes = new Command(bytes);
    assertTrue(fromBytes.getType() == CommandTypes.DRIVE_BACKWARD && fromBytes.getParam() == 300);
  }

  @Test
  public void checkSymmetrieOfCommandAsByteSerialization() {
    Random rand = new Random();

    Command command = new Command(CommandTypes.OVERTAKE, rand.nextInt(7));
    Command clone = new Command(command.toNetworkByteArray());

    assertTrue(command.equals(clone));
  }

  @Test
  public void checkSymmetrieOfCommandAsIntSerialization() {
    Random rand = new Random();

    Command command = new Command(CommandTypes.OVERTAKE, rand.nextInt(7));
    Command clone = new Command(command.toNetworkIntArray());

    assertTrue(command.equals(clone));
  }
}