import static org.junit.Assert.assertEquals;

import messaging.strategies.Strategies;
import messaging.strategies.StrategyHelper;
import org.junit.Test;


public class StrategyHelperTest {

  @Test
  public void testStrategyFromInt() {
    Strategies strategy = StrategyHelper.strategyFromInt(0);
    assertEquals(strategy, Strategies.MANUAL);
    strategy = StrategyHelper.strategyFromInt(1);
    assertEquals(strategy, Strategies.PID);

  }

  @Test
  public void testIntFromStrategy() {
    int pid = StrategyHelper.intFromStrategy(Strategies.PID);
    assertEquals(1, pid);
  }
}
