package context.concrete;

import context.Context;
import context.ContextObserver;
import context.dto.ContextInfoModel;

import java.util.Collection;

public class Editor implements Context {

    private final Collection<ContextObserver> observers;

    public Editor(Collection<ContextObserver> observers) {
        this.observers = observers;
    }

    @Override
    public void input(Integer ch) {

    }

    @Override
    public void attachObserver(ContextObserver observer) {
        observers.add(observer);
    }

    @Override
    public void detachObserver(ContextObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(ContextInfoModel info) {
        observers.forEach(m -> m.setInfo(info));
    }


}
