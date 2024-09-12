package com.ll.goohaeyou.notification.domain;

import com.ll.goohaeyou.global.event.notification.*;
import com.ll.goohaeyou.global.standard.anotations.DomainService;
import com.ll.goohaeyou.jobApplication.domain.entity.JobApplication;
import com.ll.goohaeyou.jobPost.comment.domain.Comment;
import com.ll.goohaeyou.jobPost.jobPost.domain.entity.JobPost;
import com.ll.goohaeyou.member.member.domain.entity.Member;
import com.ll.goohaeyou.notification.domain.entity.Notification;
import com.ll.goohaeyou.notification.domain.repository.NotificationRepository;
import com.ll.goohaeyou.notification.domain.type.CauseTypeCode;
import com.ll.goohaeyou.notification.domain.type.ResultTypeCode;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.ll.goohaeyou.notification.domain.type.CauseTypeCode.*;
import static com.ll.goohaeyou.notification.domain.type.ResultTypeCode.*;

@DomainService
@RequiredArgsConstructor
public class NotificationEventDomainService {
    private final NotificationRepository notificationRepository;
    private final NotificationSender notificationSender;

    @Transactional
    public void notifyApplicantsAboutPost(ChangeOfPostEvent event) {
        JobPost jobPost = event.getJobPost();
        JobApplication jobApplication = event.getJobApplication();
        String url = "/job-post/" + jobPost.getId();

        makeNotification(jobApplication.getMember(), jobPost.getMember(), jobPost.getTitle(), event.getCauseTypeCode(), NOTICE, url);
    }

    @Transactional
    public void deleteApplicationNotification(ChangeOfPostEvent event) {
        JobPost jobPost = event.getJobPost();
        JobApplication jobApplication = event.getJobApplication();
        String url = "/job-post/" + jobPost.getId();

        makeNotification(jobApplication.getMember(), jobPost.getMember(), jobPost.getTitle(),
                event.getCauseTypeCode(), DELETE, url);
    }

    @Transactional
    public void postDeletedNotification(PostDeletedEvent event) {
        JobPost jobPost = event.getJobPost();
        Member fromMember = event.getMember();
        String url = "/";

        List<JobApplication> jobApplicationList = jobPost.getJobPostDetail().getJobApplications();

        for (JobApplication jobApplication : jobApplicationList) {
            makeNotification(jobApplication.getMember(), fromMember, jobPost.getTitle(),
                    POST_DELETED, DELETE, url);
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
        makeNotification(
                jobPost.getMember(), jobPost.getMember(), jobPost.getTitle(), POST_DEADLINE, APPROVE, url
        );
    }

    @Transactional
    public void calculateNotificationEventListen(JobApplication jobApplication, JobPost jobPost) {
        String url = "/applications/detail/" + jobApplication.getId();

        makeNotification(
                jobApplication.getMember(), jobPost.getMember(), jobPost.getTitle(), CALCULATE_PAYMENT, NOTICE, url
        );
    }

    @Transactional
    public void notifyAboutChatRoom(Member member1, Member member2, String title, Long roomId) {
        String url = "/chat/" + roomId;

        makeNotification(member1, member2, title, CHATROOM_CREATED, NOTICE, url);
        makeNotification(member2, member1, title, CHATROOM_CREATED, NOTICE, url);
    }

    @Transactional
    public void postGetInterestedNotification(PostGetInterestedEvent event) {
        JobPost jobPost = event.getJobPostDetail().getJobPost();
        Member member = event.getMember();
        String url = "/job-post/" + jobPost.getId();

        makeNotification(
                jobPost.getMember() ,member, jobPost.getTitle(), POST_INTERESTED, NOTICE, url
        );
    }

    @Transactional
    public void makeNotification(Member toMember, Member fromMember, String jobPostTitle,
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
    }
}
