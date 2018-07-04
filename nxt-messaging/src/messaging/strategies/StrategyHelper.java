package messaging.strategies;

public class StrategyHelper {


  public static Strategies strategyFromInt(int param) {
    Strategies startegy = Strategies.values()[param];
    return startegy;
  }

  public static int intFromStrategy(Strategies strategy) {
    return strategy.ordinal();
  }
}
