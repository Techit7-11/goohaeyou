package com.ll.goohaeyou.notification.application;

import com.ll.goohaeyou.chat.room.domain.repository.RoomRepository;
import com.ll.goohaeyou.global.event.notification.*;
import com.ll.goohaeyou.global.exception.EntityNotFoundException;
import com.ll.goohaeyou.jobApplication.domain.JobApplication;
import com.ll.goohaeyou.jobPost.comment.domain.Comment;
import com.ll.goohaeyou.jobPost.jobPost.domain.JobPost;
import com.ll.goohaeyou.jobPost.jobPost.domain.repository.JobPostRepository;
import com.ll.goohaeyou.member.member.domain.Member;
import com.ll.goohaeyou.member.member.domain.repository.MemberRepository;
import com.ll.goohaeyou.notification.application.dto.NotificationDto;
import com.ll.goohaeyou.notification.domain.Notification;
import com.ll.goohaeyou.notification.domain.NotificationSender;
import com.ll.goohaeyou.notification.domain.repository.NotificationRepository;
import com.ll.goohaeyou.notification.domain.type.CauseTypeCode;
import com.ll.goohaeyou.notification.domain.type.ResultTypeCode;
import com.ll.goohaeyou.notification.exception.NotificationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.ll.goohaeyou.notification.domain.type.CauseTypeCode.*;
import static com.ll.goohaeyou.notification.domain.type.ResultTypeCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final JobPostRepository jobPostRepository;
    private final NotificationSender notificationSender;
    private final MemberRepository memberRepository;
    private final RoomRepository roomRepository;

    @Transactional
    public void notifyApplicantsAboutPost(ChangeOfPostEvent event) {
        JobPost jobPost = event.getJobPost();
        JobApplication jobApplication = event.getJobApplication();
        String url = "/job-post/" + jobPost.getId();

        makeNotification(jobApplication.getMember(), jobPost.getMember(),
                jobPost.getTitle(), event.getCauseTypeCode(), NOTICE, url);
    }

    @Transactional
    public void deleteApplicationNotification(ChangeOfPostEvent event) {
        JobPost jobPost = event.getJobPost();
        JobApplication jobApplication = event.getJobApplication();
        String url = "/job-post/" + jobPost.getId();

        makeNotification(jobApplication.getMember(), jobPost.getMember(), jobPost.getTitle(),
                event.getCauseTypeCode(), DELETE,url);
    }

    @Transactional
    public void postDeletedNotification(PostDeletedEvent event) {
        JobPost jobPost = event.getJobPost();
        Member fromMember = event.getMember();
        String url = "/";

        List<JobApplication> jobApplicationList = jobPost.getJobPostDetail().getJobApplications();

        for (JobApplication jobApplication : jobApplicationList) {
            makeNotification(jobApplication.getMember(), fromMember, jobPost.getTitle(),
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
    public void applicationCreatedAndChangedNotification(JobApplicationCreateAndChangedEvent event) {
        JobPost jobPost = event.getJobPostDetail().getJobPost();
        JobApplication jobApplication = event.getJobApplication();
        String url = "/applications/detail/" + jobApplication.getId();
        makeNotification(jobPost.getMember(), jobApplication.getMember(), jobPost.getTitle(),
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
    public void calculateNotificationEventListen(JobApplication jobApplication) {
        JobPost jobPost = jobPostRepository.findById(jobApplication.getJobPostDetail().getId())
                .orElseThrow(EntityNotFoundException.PostNotExistsException::new);
        String url = "/applications/detail/" + jobApplication.getId();

        makeNotification(jobApplication.getMember(), jobPost.getMember(), jobPost.getTitle(),
                CALCULATE_PAYMENT, NOTICE, url);
    }

    @Transactional
    public void notifyAboutChatRoom(CreateChatRoomEvent event) {
        log.info("알림 생성 중");

        Member member1 = memberRepository.findById(event.getMemberId1())
                .orElseThrow(EntityNotFoundException.MemberNotFoundException::new);
        Member member2 = memberRepository.findById(event.getMemberId2())
                .orElseThrow(EntityNotFoundException.MemberNotFoundException::new);
        String title = event.getPostTitle();
        Long roomId = roomRepository.findByUsername1AndUsername2(member1.getUsername(), member2.getUsername())
                .orElseGet(() -> roomRepository.findByUsername1AndUsername2(member2.getUsername(), member1.getUsername())
                        .orElseThrow(EntityNotFoundException.ChatroomNotExistsException::new))
                .getId();

        String url = "/chat/" + roomId;
        makeNotification(member1, member2, title, CHATROOM_CREATED, NOTICE, url);
        makeNotification(member2, member1, title, CHATROOM_CREATED, NOTICE, url);

        log.info("알림 생성 완료");
    }

    private NotificationDto makeNotification(Member toMember, Member fromMember, String jobPostTitle,
                                  CauseTypeCode causeTypeCode, ResultTypeCode resultTypeCode, String url) {
        Notification newNotification = Notification.create(
                toMember,
                fromMember,
                jobPostTitle,
                causeTypeCode,
                resultTypeCode,
                url
        );

        notificationRepository.save(newNotification);

        if (toMember.getFCMToken() != null) {
            notificationSender.send(toMember.getFCMToken(), jobPostTitle, causeTypeCode);
        }

        return NotificationDto.from(newNotification);
    }

    public List<NotificationDto> getList(String username) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(EntityNotFoundException.MemberNotFoundException::new);
        List<Notification> notificationList = notificationRepository.findByToMemberOrderByCreateAtDesc(member);

        return NotificationDto.convertToDtoList(notificationList);
    }

    @Transactional
    public void deleteReadAllNotification(String username) {
        Member toMember = memberRepository.findByUsername(username)
                .orElseThrow(EntityNotFoundException.MemberNotFoundException::new);
        List<Notification> readNotification = notificationRepository.findByToMemberAndSeenIsTrue(toMember);

        notificationRepository.deleteAll(readNotification);
    }

    @Transactional
    public void deleteAllNotification(String username) {
        Member toMember = memberRepository.findByUsername(username)
                .orElseThrow(EntityNotFoundException.MemberNotFoundException::new);
        List<Notification> removeNotification = notificationRepository.findByToMember(toMember);
        notificationRepository.deleteAll(removeNotification);
    }

    @Transactional
    public void readNotification(String username, Long notificationId) {
        memberRepository.findByUsername(username)
                .orElseThrow(EntityNotFoundException.MemberNotFoundException::new);
        Notification notification = findByIdAndValidate(notificationId);
        notification.update();
    }

    private Notification findByIdAndValidate (Long notificationId) {
        return notificationRepository.findById(notificationId)
                .orElseThrow(NotificationException.NotificationNotExistsException::new);
    }

    public Boolean unreadNotification(String username) {
        Member toMember = memberRepository.findByUsername(username)
                .orElseThrow(EntityNotFoundException.MemberNotFoundException::new);

        return notificationRepository.existsByToMemberAndSeenIsFalse(toMember);
    }

    @Transactional
    public void register(Long userId, String token) {
        int startIndex = token.indexOf("\"token\":\"") + "\"token\":\"".length();
        int endIndex = token.lastIndexOf("\"");

        String extractedToken = token.substring(startIndex, endIndex);

        Member member = memberRepository.findById(userId)
                        .orElseThrow(EntityNotFoundException.MemberNotFoundException::new);
        member.registerFCMToken(extractedToken);

        memberRepository.save(member);
    }

    @Transactional
    public void deleteToken(Long userId) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(EntityNotFoundException.MemberNotFoundException::new);
        member.removeFCMToken();

        memberRepository.save(member);
    }
}
