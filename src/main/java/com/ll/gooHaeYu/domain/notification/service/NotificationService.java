package com.ll.gooHaeYu.domain.notification.service;

import com.ll.gooHaeYu.domain.application.application.entity.Application;
import com.ll.gooHaeYu.domain.jobPost.comment.entity.Comment;
import com.ll.gooHaeYu.domain.jobPost.jobPost.entity.JobPost;
import com.ll.gooHaeYu.domain.member.member.entity.Member;
import com.ll.gooHaeYu.domain.notification.entity.Notification;
import com.ll.gooHaeYu.domain.notification.entity.type.CauseTypeCode;
import com.ll.gooHaeYu.domain.notification.entity.type.ResultTypeCode;
import com.ll.gooHaeYu.domain.notification.repository.NotificationRepository;
import com.ll.gooHaeYu.global.event.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.ll.gooHaeYu.domain.notification.entity.type.CauseTypeCode.*;
import static com.ll.gooHaeYu.domain.notification.entity.type.ResultTypeCode.DELETE;
import static com.ll.gooHaeYu.domain.notification.entity.type.ResultTypeCode.NOTICE;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class NotificationService {
    private final NotificationRepository notificationRepository;

    @Transactional
    public void notifyApplicantsAboutPost(ChangeOfPostEvent event) {
        log.debug("이벤트 로직 실행");
        JobPost jobPost = event.getJobPost();
        Application application = event.getApplication();
        makeNotification(application.getMember(),jobPost.getMember(),jobPost.getTitle(),event.getCauseTypeCode(),NOTICE);
    }

    @Transactional
    public void deleteApplicationNotification(ChangeOfPostEvent event) {
        log.debug("이벤트 로직 실행");
        JobPost jobPost = event.getJobPost();
        Application application = event.getApplication();
        makeNotification(application.getMember(),jobPost.getMember(),jobPost.getTitle(),event.getCauseTypeCode(),DELETE);
    }

    @Transactional
    public void postDeletedNotification(PostDeletedEvent event) {
        log.debug("이벤트 로직 실행");
        JobPost jobPost = event.getJobPost();
        Member fromMember = event.getMember();
        List<Application> applicationList = jobPost.getJobPostDetail().getApplications();
        for (Application application : applicationList) {
            makeNotification(application.getMember(),fromMember,jobPost.getTitle(),POST_DELETED,DELETE);
        }
    }

    @Transactional
    public void commentCreatedNotification(CommentCreatedEvent event) {
        JobPost jobPost = event.getJobPostDetail().getJobPost();
        Comment comment = event.getComment();
        makeNotification(jobPost.getMember(), comment.getMember(), jobPost.getTitle(), COMMENT_CREATED, NOTICE);
    }

    @Transactional
    public void postGetInterestedNotification(PostGetInterestedEvent event) {
        JobPost jobPost = event.getJobPostDetail().getJobPost();
        Member member = event.getMember();
        makeNotification(jobPost.getMember(),member, jobPost.getTitle(), POST_INTERESTED, NOTICE);
    }

    @Transactional
    public void applicationCreatedAndChangedNotification(ApplicationCreateAndChangedEvent event) {
        JobPost jobPost = event.getJobPostDetail().getJobPost();
        Application application = event.getApplication();
        makeNotification(jobPost.getMember(),application.getMember(), jobPost.getTitle(), event.getCauseTypeCode(), NOTICE);
    }

    private void makeNotification(Member toMember, Member fromMember, String jobPostTitle, CauseTypeCode causeTypeCode, ResultTypeCode resultTypeCode) {
        Notification notification = Notification.builder()
                .createAt(LocalDateTime.now())
                .toMember(toMember)
                .fromMember(fromMember)
                .relPostTitle(jobPostTitle)
                .causeTypeCode(causeTypeCode)
                .resultTypeCode(resultTypeCode)
                .seen(false)
                .build();

        notificationRepository.save(notification);
        log.debug("알림 생성");
    }
}
