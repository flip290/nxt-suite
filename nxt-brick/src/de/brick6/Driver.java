package de.brick6;

import de.brick6.Strategies.Backwards;
import de.brick6.Strategies.LineFinder;
import de.brick6.Strategies.Manual;
import de.brick6.Strategies.PidStrategy;
import de.brick6.Strategies.Overtaker;
import messaging.comtoandroid.StateData;
import messaging.comtoandroid.StateTypes;
import messaging.strategies.StrategyHelper;

public final class Driver implements IObserver {
    private Facade facade;
    private Strategy strategy;

    void setStrategy(final Strategy value) {
        strategy.pause();
        PidStrategy.get().reset();
        strategy = value;
        strategy.start();
        StateDataHandler.getInstance().sendStateData(new StateData(StateTypes.STRATEGY, StrategyHelper.intFromStrategy(value.getEnum())));
    }

    Strategy getStrategy() {
        return strategy;
    }

    Driver() {
        LightSensor.get().setFloodlight(true);
        Manual.get().addObserver(this);
        Backwards.get().addObserver(this);
        LineFinder.get().addObserver(this);
        strategy = PidStrategy.get();
        strategy.addObserver(this);

        facade = Facade.getInstance();
        facade.setDriver(this);
        facade.addObserver(this);
        try {
            facade.connect();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        Logger.get().log("start");
        strategy.start();
    }


    public void update(final Observable observable, final String event) {
        Logger.get().log(event);
        if (event.equals("LostLineEvent")) {
            setStrategy(Manual.get());
            LeftMotor.get().setSpeed(0);
            RightMotor.get().setSpeed(0);
        } else if (event.equals("FoundLineEvent")) {
            setStrategy(PidStrategy.get());
        } else if (event.equals("ConnectedEvent")) {
            try {
                facade.listen();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } else if (event.equals("DisconnectedEvent")) {
            try {
                facade.connect();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } else if (event.equals("Overtake")) {
            setStrategy(Overtaker.get());
        }
    }
}
