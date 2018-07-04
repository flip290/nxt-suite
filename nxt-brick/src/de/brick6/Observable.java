package de.brick6;

import java.util.ArrayList;

public class Observable {
    private ArrayList<IObserver> observerSet = new ArrayList<IObserver>();

    public final void addObserver(final IObserver observer) {
        observerSet.add(observer);
    }

    protected final void notifyObservers(final String event) {
        for (int i = 0; i < observerSet.size(); i++) {
            IObserver observer = observerSet.get(i);
            observer.update(this, event);
        }
    }
}
