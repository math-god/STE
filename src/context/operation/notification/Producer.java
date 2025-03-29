package context.operation.notification;

public interface Producer {
    void attachConsumer(Consumer consumer);

    void detachConsumer(Consumer consumer);
}
