package com.ll.goohaeyou.notification.domain;

import com.ll.goohaeyou.notification.domain.type.CauseTypeCode;

public interface NotificationSender {
    void send(String token, String postTitle, CauseTypeCode causeTypeCode);
}
