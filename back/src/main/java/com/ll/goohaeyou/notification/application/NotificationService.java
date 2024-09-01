package com.ll.goohaeyou.notification.application;

import com.ll.goohaeyou.application.domain.Application;
import com.ll.goohaeyou.chat.room.application.RoomService;
import com.ll.goohaeyou.jobPost.comment.domain.Comment;
import com.ll.goohaeyou.jobPost.jobPost.domain.JobPost;
import com.ll.goohaeyou.jobPost.jobPost.application.JobPostService;
import com.ll.goohaeyou.member.member.domain.Member;
import com.ll.goohaeyou.member.member.domain.repository.MemberRepository;
import com.ll.goohaeyou.member.member.application.MemberService;
import com.ll.goohaeyou.notification.application.dto.NotificationDto;
import com.ll.goohaeyou.notification.domain.Notification;
import com.ll.goohaeyou.notification.domain.repository.NotificationRepository;
import com.ll.goohaeyou.notification.domain.type.CauseTypeCode;
import com.ll.goohaeyou.notification.domain.type.ResultTypeCode;
import com.ll.goohaeyou.global.event.notification.*;
import com.ll.goohaeyou.notification.exception.NotificationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.ll.goohaeyou.notification.domain.type.CauseTypeCode.*;
import static com.ll.goohaeyou.notification.domain.type.ResultTypeCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final MemberService memberService;
    private final RoomService roomService;
    private final JobPostService jobPostService;
    private final FCMService fcmService;
    private final MemberRepository memberRepository;

    @Transactional
    public void notifyApplicantsAboutPost(ChangeOfPostEvent event) {
        JobPost jobPost = event.getJobPost();
        Application application = event.getApplication();
        String url = "/job-post/" + jobPost.getId();

        makeNotification(application.getMember(), jobPost.getMember(),
                jobPost.getTitle(), event.getCauseTypeCode(), NOTICE, url);
    }

    @Transactional
    public void deleteApplicationNotification(ChangeOfPostEvent event) {
        JobPost jobPost = event.getJobPost();
        Application application = event.getApplication();
        String url = "/job-post/" + jobPost.getId();

        makeNotification(application.getMember(), jobPost.getMember(), jobPost.getTitle(),
                event.getCauseTypeCode(), DELETE,url);
    }

    @Transactional
    public void postDeletedNotification(PostDeletedEvent event) {
        JobPost jobPost = event.getJobPost();
        Member fromMember = event.getMember();
        String url = "/";

        List<Application> applicationList = jobPost.getJobPostDetail().getApplications();

        for (Application application : applicationList) {
            makeNotification(application.getMember(), fromMember, jobPost.getTitle(),
                    POST_DELETED,DELETE, url);
        }
    }

    @Transactional
    public void commentCreatedNotification(CommentCreatedEvent event) {
        JobPost jobPost = event.getJobPostDetail().getJobPost();
        Comment comment = event.getComment();
        String url = "/job-post/" + jobPost.getId();

        makeNotification(jobPost.getMember(), comment.getMember(), jobPost.getTitle(),
                COMMENT_CREATED, NOTICE, url);
    }

    @Transactional
    public void postGetInterestedNotification(PostGetInterestedEvent event) {
        JobPost jobPost = event.getJobPostDetail().getJobPost();
        Member member = event.getMember();
        String url = "/job-post/" + jobPost.getId();

        makeNotification(jobPost.getMember() ,member, jobPost.getTitle(),
                POST_INTERESTED, NOTICE, url);
    }

    @Transactional
    public void applicationCreatedAndChangedNotification(ApplicationCreateAndChangedEvent event) {
        JobPost jobPost = event.getJobPostDetail().getJobPost();
        Application application = event.getApplication();
        String url = "/applications/detail/" + application.getId();
        makeNotification(jobPost.getMember(), application.getMember(), jobPost.getTitle(),
                event.getCauseTypeCode(), NOTICE, url);
    }

    @Transactional
    public void jobPostClosedNotificationEventListen(PostDeadlineEvent event) {
        JobPost jobPost = event.getJobPost();
        String url = "/job-post/" + jobPost.getId();
        makeNotification(jobPost.getMember(), jobPost.getMember(), jobPost.getTitle(),
                POST_DEADLINE, APPROVE, url);
    }

    @Transactional
    public void calculateNotificationEventListen(Application application) {
        Member member = application.getMember();
        JobPost jobPost = jobPostService.findByIdAndValidate(application.getJobPostDetail().getId());
        String url = "/applications/detail/" + application.getId();
        makeNotification(application.getMember(), jobPost.getMember(), jobPost.getTitle(),
                CALCULATE_PAYMENT, NOTICE, url);
    }

    @Transactional
    public void notifyAboutChatRoom(CreateChatRoomEvent event) {
        log.info("알림 생성 중");

        Member member1 = memberService.findById(event.getMemberId1());
        Member member2 = memberService.findById(event.getMemberId2());
        String title = event.getPostTitle();
        Long roomId = roomService.findByUsername1AndUsername2(member1.getUsername(), member2.getUsername()).getId();

        String url = "/chat/" + roomId;
        makeNotification(member1, member2, title, CHATROOM_CREATED, NOTICE, url);
        makeNotification(member2, member1, title, CHATROOM_CREATED, NOTICE, url);

        log.info("알림 생성 완료");
    }

    private void makeNotification(Member toMember, Member fromMember, String jobPostTitle, CauseTypeCode causeTypeCode,
                                  ResultTypeCode resultTypeCode, String url) {
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

        if (toMember.getFCMToken() != null) {
            fcmService.send(toMember.getFCMToken(), jobPostTitle, causeTypeCode);
        }
    }

    public List<NotificationDto> getList(String username) {
        Member member = memberService.getMember(username);
        List<Notification> notificationList = notificationRepository.findByToMemberOrderByCreateAtDesc(member);

        return NotificationDto.convertToDtoList(notificationList);
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
        memberService.getMember(username);
        Notification notification = findByIdAndValidate(notificationId);
        notification.update();
    }

    private Notification findByIdAndValidate (Long notificationId) {
        return notificationRepository.findById(notificationId)
                .orElseThrow(NotificationException.NotificationNotExistsException::new);
    }

    public Boolean unreadNotification(String username) {
        Member toMember = memberService.getMember(username);

        return notificationRepository.existsByToMemberAndSeenIsFalse(toMember);
    }

    @Transactional
    public void register(Long userId, String token) {
        int startIndex = token.indexOf("\"token\":\"") + "\"token\":\"".length();
        int endIndex = token.lastIndexOf("\"");

        String extractedToken = token.substring(startIndex, endIndex);

        Member member = memberService.findById(userId);
        member.registerFCMToken(extractedToken);

        memberRepository.save(member);
    }

    @Transactional
    public void deleteToken(Long userId) {
        Member member = memberService.findById(userId);
        member.removeFCMToken();

        memberRepository.save(member);
    }
}
