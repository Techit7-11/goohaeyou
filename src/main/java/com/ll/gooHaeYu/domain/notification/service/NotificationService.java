package com.ll.gooHaeYu.domain.notification.service;

import com.ll.gooHaeYu.domain.application.application.entity.Application;
import com.ll.gooHaeYu.domain.chat.room.service.RoomService;
import com.ll.gooHaeYu.domain.jobPost.comment.entity.Comment;
import com.ll.gooHaeYu.domain.jobPost.jobPost.entity.JobPost;
import com.ll.gooHaeYu.domain.jobPost.jobPost.entity.JobPostDetail;
import com.ll.gooHaeYu.domain.jobPost.jobPost.service.JobPostService;
import com.ll.gooHaeYu.domain.member.member.entity.Member;
import com.ll.gooHaeYu.domain.member.member.service.MemberService;
import com.ll.gooHaeYu.domain.notification.dto.NotificationDto;
import com.ll.gooHaeYu.domain.notification.entity.Notification;
import com.ll.gooHaeYu.domain.notification.entity.type.CauseTypeCode;
import com.ll.gooHaeYu.domain.notification.entity.type.ResultTypeCode;
import com.ll.gooHaeYu.domain.notification.repository.NotificationRepository;
import com.ll.gooHaeYu.global.event.notification.*;
import com.ll.gooHaeYu.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.ll.gooHaeYu.domain.notification.entity.type.CauseTypeCode.*;
import static com.ll.gooHaeYu.domain.notification.entity.type.ResultTypeCode.*;
import static com.ll.gooHaeYu.global.exception.ErrorCode.NOTIFICATION_NOT_EXIST;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final MemberService memberService;
    private final RoomService roomService;
    private final JobPostService jobPostService;

    @Transactional
    public void notifyApplicantsAboutPost(ChangeOfPostEvent event) {
        JobPost jobPost = event.getJobPost();
        Application application = event.getApplication();
        String url = "/job-post/"+jobPost.getId();
        makeNotification(application.getMember(),jobPost.getMember(),jobPost.getTitle(),event.getCauseTypeCode(),NOTICE, url);
    }

    @Transactional
    public void deleteApplicationNotification(ChangeOfPostEvent event) {
        JobPost jobPost = event.getJobPost();
        Application application = event.getApplication();
        String url = "/job-post/"+jobPost.getId();
        makeNotification(application.getMember(),jobPost.getMember(),jobPost.getTitle(),event.getCauseTypeCode(),DELETE,url);
    }

    @Transactional
    public void postDeletedNotification(PostDeletedEvent event) {
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

    @Transactional
    public void jobPostClosedNotificationEventListen(PostDeadlineEvent event) {
        JobPost jobPost = event.getJobPost();
        String url = "/job-post/"+jobPost.getId();
        makeNotification(jobPost.getMember(),jobPost.getMember(), jobPost.getTitle(),POST_DEADLINE,APPROVE,url);
    }

    @Transactional
    public void calculateNotificationEventListen(Application application) {
        Member member = application.getMember();
        JobPost jobPost = jobPostService.findByIdAndValidate(application.getJobPostDetail().getId());
        String url = "/applications/detail/"+application.getId();
        makeNotification(application.getMember(), jobPost.getMember(), jobPost.getTitle(),CALCULATE_PAYMENT, NOTICE, url);
    }

    @Transactional
    public void notifyAboutChatRoom(CreateChatRoomEvent event) {
        log.info("알림 생성 중");
        Member member1 = memberService.findById(event.getMemberId1());
        Member member2 = memberService.findById(event.getMemberId2());
        String title = event.getPostTitle();
        Long roomId = roomService.findByUsername1AndUsername2(member1.getUsername(), member2.getUsername()).getId();

        String url = "/chat/"+roomId;
        makeNotification(member1,member2,title,CHATROOM_CREATED,NOTICE, url);
        makeNotification(member2,member1,title,CHATROOM_CREATED,NOTICE, url);
        log.info("알림 생성 완료");
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

    public Boolean unreadNotification(String username) {
        Member toMember = memberService.getMember(username);
        return notificationRepository.existsByToMemberAndSeenIsFalse(toMember);
    }
}
