package com.ll.gooHaeYu.domain.notification.eventListener;

import com.ll.gooHaeYu.global.event.EventAfterApplication;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional
@Slf4j
public class NotificationEventListener {

    @EventListener
    public void listen(EventAfterApplication event) {
        // 지원서 작성 시
        log.debug("EventAfterApplication event : {}",event);
    }
}
