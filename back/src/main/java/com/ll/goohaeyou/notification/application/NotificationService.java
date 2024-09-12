package com.ll.goohaeyou.notification.application;

import com.ll.goohaeyou.chat.room.domain.RoomDomainService;
import com.ll.goohaeyou.global.event.notification.*;
import com.ll.goohaeyou.global.standard.base.util.FcmTokenUtil;
import com.ll.goohaeyou.jobApplication.domain.entity.JobApplication;
import com.ll.goohaeyou.jobPost.jobPost.domain.JobPostDomainService;
import com.ll.goohaeyou.jobPost.jobPost.domain.entity.JobPost;
import com.ll.goohaeyou.member.member.domain.MemberDomainService;
import com.ll.goohaeyou.member.member.domain.entity.Member;
import com.ll.goohaeyou.notification.application.dto.NotificationResponse;
import com.ll.goohaeyou.notification.domain.NotificationEventDomainService;
import com.ll.goohaeyou.notification.domain.NotificationManagementDomainService;
import com.ll.goohaeyou.notification.domain.entity.Notification;
import com.ll.goohaeyou.notification.domain.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final MemberDomainService memberDomainService;
    private final JobPostDomainService jobPostDomainService;
    private final RoomDomainService roomDomainService;
    private final NotificationEventDomainService notificationEventDomainService;
    private final NotificationManagementDomainService notificationManagementDomainService;

    @Transactional
    public void notifyApplicantsAboutPost(ChangeOfPostEvent event) {
        notificationEventDomainService.notifyApplicantsAboutPost(event);
    }

    @Transactional
    public void deleteApplicationNotification(ChangeOfPostEvent event) {
        notificationEventDomainService.deleteApplicationNotification(event);
    }

    @Transactional
    public void postDeletedNotification(PostDeletedEvent event) {
        notificationEventDomainService.postDeletedNotification(event);
    }

    @Transactional
    public void commentCreatedNotification(CommentCreatedEvent event) {
        notificationEventDomainService.commentCreatedNotification(event);
    }

    @Transactional
    public void postGetInterestedNotification(PostGetInterestedEvent event) {
        notificationEventDomainService.postGetInterestedNotification(event);
    }

    @Transactional
    public void applicationCreatedAndChangedNotification(JobApplicationCreateAndChangedEvent event) {
        notificationEventDomainService.applicationCreatedAndChangedNotification(event);
    }

    @Transactional
    public void jobPostClosedNotificationEventListen(PostDeadlineEvent event) {
        notificationEventDomainService.jobPostClosedNotificationEventListen(event);
    }

    @Transactional
    public void calculateNotificationEventListen(JobApplication jobApplication) {
        JobPost jobPost = jobPostDomainService.findById(jobApplication.getJobPostDetail().getId());

        notificationEventDomainService.calculateNotificationEventListen(jobApplication, jobPost);
    }

    @Transactional
    public void notifyAboutChatRoom(CreateChatRoomEvent event) {
        log.info("알림 생성 중");

        Member member1 = memberDomainService.getById(event.getMemberId1());
        Member member2 = memberDomainService.getById(event.getMemberId2());
        String title = event.getPostTitle();
        Long roomId = roomDomainService.getByUsername1AndUsername2(member1.getUsername(), member2.getUsername()).getId();

        notificationEventDomainService.notifyAboutChatRoom(member1, member2, title, roomId);

        log.info("알림 생성 완료");
    }

    public List<NotificationResponse> getList(String username) {
        Member member = memberDomainService.getByUsername(username);

        List<Notification> notificationList =
                notificationManagementDomainService.getByToMemberOrderByCreateAtDesc(member);

        return NotificationResponse.convertToDtoList(notificationList);
    }

    @Transactional
    public void deleteReadAllNotification(String username) {
        Member toMember = memberDomainService.getByUsername(username);
        List<Notification> readNotification = notificationRepository.findByToMemberAndSeenIsTrue(toMember);

        notificationRepository.deleteAll(readNotification);
    }

    @Transactional
    public void deleteAllNotification(String username) {
        Member toMember = memberDomainService.getByUsername(username);

        List<Notification> removeNotification = notificationManagementDomainService.getByToMember(toMember);

        notificationManagementDomainService.deleteAll(removeNotification);
    }

    @Transactional
    public void readNotification(String username, Long notificationId) {
        memberDomainService.getByUsername(username);

        Notification notification = notificationManagementDomainService.getById(notificationId);

        notification.update();
    }

    public Boolean unreadNotification(String username) {
        Member toMember = memberDomainService.getByUsername(username);

        return notificationManagementDomainService.existsByToMemberAndSeenIsFalse(toMember);
    }

    @Transactional
    public void register(Long userId, String token) {
        String extractedToken = FcmTokenUtil.parseFcmToken(token);

        Member member = memberDomainService.getById(userId);
        member.registerFCMToken(extractedToken);

        memberDomainService.save(member);
    }

    @Transactional
    public void deleteToken(Long userId) {
        Member member = memberDomainService.getById(userId);

        member.removeFCMToken();

        memberDomainService.save(member);
    }
}
