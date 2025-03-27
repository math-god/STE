package context;

import common.Action;

public interface Observable {

    void attachObserver(Observer observer);

    void detachObserver(Observer observer);
}
