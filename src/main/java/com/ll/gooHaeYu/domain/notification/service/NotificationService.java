package com.ll.gooHaeYu.domain.notification.service;

import com.ll.gooHaeYu.domain.application.application.dto.ApplicationDto;
import com.ll.gooHaeYu.domain.application.application.entity.Application;
import com.ll.gooHaeYu.domain.jobPost.comment.entity.Comment;
import com.ll.gooHaeYu.domain.jobPost.jobPost.entity.JobPost;
import com.ll.gooHaeYu.domain.jobPost.jobPost.entity.JobPostDetail;
import com.ll.gooHaeYu.domain.member.member.entity.Member;
import com.ll.gooHaeYu.domain.member.member.service.MemberService;
import com.ll.gooHaeYu.domain.notification.dto.NotificationDto;
import com.ll.gooHaeYu.domain.notification.entity.Notification;
import com.ll.gooHaeYu.domain.notification.entity.type.CauseTypeCode;
import com.ll.gooHaeYu.domain.notification.entity.type.ResultTypeCode;
import com.ll.gooHaeYu.domain.notification.repository.NotificationRepository;
import com.ll.gooHaeYu.global.event.*;
import com.ll.gooHaeYu.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.ll.gooHaeYu.domain.notification.entity.type.CauseTypeCode.*;
import static com.ll.gooHaeYu.domain.notification.entity.type.ResultTypeCode.DELETE;
import static com.ll.gooHaeYu.domain.notification.entity.type.ResultTypeCode.NOTICE;
import static com.ll.gooHaeYu.global.exception.ErrorCode.NOTIFICATION_NOT_EXIST;
import static com.ll.gooHaeYu.global.exception.ErrorCode.POST_NOT_EXIST;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final MemberService memberService;

    @Transactional
    public void notifyApplicantsAboutPost(ChangeOfPostEvent event) {
        log.debug("이벤트 로직 실행");
        JobPost jobPost = event.getJobPost();
        Application application = event.getApplication();
        String url = "/job-post/"+jobPost.getId();
        makeNotification(application.getMember(),jobPost.getMember(),jobPost.getTitle(),event.getCauseTypeCode(),NOTICE, url);
    }

    @Transactional
    public void deleteApplicationNotification(ChangeOfPostEvent event) {
        log.debug("이벤트 로직 실행");
        JobPost jobPost = event.getJobPost();
        Application application = event.getApplication();
        String url = "/job-post/"+jobPost.getId();
        makeNotification(application.getMember(),jobPost.getMember(),jobPost.getTitle(),event.getCauseTypeCode(),DELETE,url);
    }

    @Transactional
    public void postDeletedNotification(PostDeletedEvent event) {
        log.debug("이벤트 로직 실행");
        JobPost jobPost = event.getJobPost();
        Member fromMember = event.getMember();
        String url = "/";
        List<Application> applicationList = jobPost.getJobPostDetail().getApplications();
        for (Application application : applicationList) {
            makeNotification(application.getMember(),fromMember,jobPost.getTitle(),POST_DELETED,DELETE,url);
        }
    }

    @Transactional
    public void commentCreatedNotification(CommentCreatedEvent event) {
        JobPost jobPost = event.getJobPostDetail().getJobPost();
        Comment comment = event.getComment();
        String url = "/job-post/"+jobPost.getId();
        makeNotification(jobPost.getMember(), comment.getMember(), jobPost.getTitle(), COMMENT_CREATED, NOTICE, url);
    }

    @Transactional
    public void postGetInterestedNotification(PostGetInterestedEvent event) {
        JobPost jobPost = event.getJobPostDetail().getJobPost();
        Member member = event.getMember();
        String url = "/job-post/"+jobPost.getId();
        makeNotification(jobPost.getMember(),member, jobPost.getTitle(), POST_INTERESTED, NOTICE, url);
    }

    @Transactional
    public void applicationCreatedAndChangedNotification(ApplicationCreateAndChangedEvent event) {
        JobPost jobPost = event.getJobPostDetail().getJobPost();
        Application application = event.getApplication();
        String url = "/applications/detail/"+application.getId();
        makeNotification(jobPost.getMember(),application.getMember(), jobPost.getTitle(), event.getCauseTypeCode(), NOTICE, url);
    }

    private void makeNotification(Member toMember, Member fromMember, String jobPostTitle, CauseTypeCode causeTypeCode, ResultTypeCode resultTypeCode, String url) {
        Notification notification = Notification.builder()
                .createAt(LocalDateTime.now())
                .toMember(toMember)
                .fromMember(fromMember)
                .relPostTitle(jobPostTitle)
                .causeTypeCode(causeTypeCode)
                .resultTypeCode(resultTypeCode)
                .seen(false)
                .url(url)
                .build();

        notificationRepository.save(notification);
        log.debug("알림 생성");
    }

    public List<NotificationDto> getList(String username) {
        Member member = memberService.getMember(username);
        List<Notification> notificationList = notificationRepository.findByToMemberOrderByCreateAtDesc(member);
        return NotificationDto.toDtoList(notificationList);
    }

    @Transactional
    public void deleteReadAllNotification(String username) {
        Member toMember = memberService.getMember(username);
        List<Notification> readNotification = notificationRepository.findByToMemberAndSeenIsTrue(toMember);
        notificationRepository.deleteAll(readNotification);
    }

    @Transactional
    public void deleteAllNotification(String username) {
        Member toMember = memberService.getMember(username);
        List<Notification> removeNotification = notificationRepository.findByToMember(toMember);
        notificationRepository.deleteAll(removeNotification);
    }

    @Transactional
    public void readNotification(String username, Long notificationId) {
        Member toMember = memberService.getMember(username);
        Notification notification = findByIdAndValidate(notificationId);
        notification.update();
    }

    private Notification findByIdAndValidate (Long notificationId) {
        return notificationRepository.findById(notificationId)
                .orElseThrow(() -> new CustomException(NOTIFICATION_NOT_EXIST));
    }
}
