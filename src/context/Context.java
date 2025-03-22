package context;

public interface Context {

    void input(Integer ch);

    void attachObserver(ContextObserver observer);

    void detachObserver(ContextObserver observer);
}
