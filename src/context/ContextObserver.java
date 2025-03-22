package context;

import context.dto.ContextNotificationModel;

public interface ContextObserver {
    void setInfo(ContextNotificationModel info);
}
