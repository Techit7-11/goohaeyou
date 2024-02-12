package com.ll.gooHaeYu.domain.notification.eventListener;

import com.ll.gooHaeYu.domain.notification.service.NotificationService;
import com.ll.gooHaeYu.global.event.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.ll.gooHaeYu.domain.notification.entity.type.ResultTypeCode.*;

@Component
@RequiredArgsConstructor
@Transactional
@Slf4j
public class NotificationEventListener {
    private final NotificationService notificationService;
    @EventListener
    public void postCorrectionEventListen(ChangeOfPostEvent event) {
        if (event.getResultTypeCode() == NOTICE) {
            // 공고 변경 시 지원서 제출한 유저에게 공고 변경 알림
            log.debug("공고 변경 알림");
            notificationService.notifyApplicantsAboutPost(event);
        }else {
            // 지원서 삭제
            log.debug("지원서 삭제 알림");
            notificationService.deleteApplicationNotification(event);
        }
    }

    @EventListener
    public void postDeletedEventListen(PostDeletedEvent event) {
        // 공고 삭제 시 지원자들에게 알림
        log.debug("공고 삭제 알림");
        notificationService.postDeletedNotification(event);
    }

    @EventListener
    public void commentCreatedEventListen(CommentCreatedEvent event) {
        // 댓글 작성 시 공고 작성자에게 알림
        log.debug("댓글 작성 알림");
        notificationService.commentCreatedNotification(event);
    }

    @EventListener
    public void postGetInterestedEventListen(PostGetInterestedEvent event) {
        // 공고 관심 등록 시 작성자에게 알림
        log.debug("관심 등록 알림");
        notificationService.postGetInterestedNotification(event);
    }

    @EventListener
    public void ApplicationCreatedAndChangedEventListen(ApplicationCreateAndChangedEvent event) {
        // 지원서 작성 시
        log.debug("지원서 작성 및 수정 알림");
        notificationService.applicationCreatedAndChangedNotification(event);
    }
}
