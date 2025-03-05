package context;

import context.dto.ContextInfoModel;

public interface ContextObserver {
    void setInfo(ContextInfoModel info);
}
