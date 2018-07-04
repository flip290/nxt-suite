import static org.junit.Assert.assertTrue;

import java.util.Random;
import messaging.comtoandroid.DataTypeFromRobot;
import messaging.comtoandroid.StateData;
import messaging.comtoandroid.StateTypes;
import org.junit.Test;

public class StateDataTest {


  @Test
  public void testByteArrayConstructor() {
    byte[] bytes = {(byte) DataTypeFromRobot.STATEDATA.ordinal(), (byte) StateTypes.SPEED.ordinal(),
        0x00, 0x00, 0x01, 0x2C};
    StateData fromBytes = new StateData(bytes);
    assertTrue(fromBytes.getType() == StateTypes.SPEED && fromBytes.getData() == 300);
  }

  @Test
  public void checkSymmetrieOfStateDataAsByteSerialization() {
    Random rand = new Random();

    StateData data = new StateData(StateTypes.STRATEGY, rand.nextInt(7));
    StateData clone = new StateData(data.toNetworkByteArray());

    assertTrue(data.equals(clone));
  }

}
