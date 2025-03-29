package context.operation.notification;

import context.dto.ContextNotificationModel;

public interface Consumer {
    void setInfo(ContextNotificationModel info);
}
