package context.operation.notification;

import java.util.Collection;
import java.util.HashSet;

public abstract class Producer {

    protected final Collection<Consumer> consumers;

    protected Producer() {
        this.consumers = new HashSet<>();
    }

    public void attachObserver(Consumer consumer) {
        consumers.add(consumer);
    }

    public void detachObserver(Consumer consumer) {
        consumers.remove(consumer);
    }
}
