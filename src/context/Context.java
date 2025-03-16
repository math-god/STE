package context;

import context.dto.ContextInfoModel;
import state.State;

public interface Context {

    void input(Integer ch);

    void attachObserver(ContextObserver observer);

    void detachObserver(ContextObserver observer);
}
