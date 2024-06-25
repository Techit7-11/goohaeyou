package com.ll.goohaeyou.domain.jobPost.employ.service;

import com.ll.goohaeyou.domain.application.dto.ApplicationDto;
import com.ll.goohaeyou.domain.application.entity.Application;
import com.ll.goohaeyou.domain.application.entity.type.WageStatus;
import com.ll.goohaeyou.domain.jobPost.jobPost.entity.JobPost;
import com.ll.goohaeyou.domain.jobPost.jobPost.entity.JobPostDetail;
import com.ll.goohaeyou.domain.jobPost.jobPost.entity.type.WagePaymentMethod;
import com.ll.goohaeyou.domain.jobPost.jobPost.service.JobPostService;
import com.ll.goohaeyou.global.event.notification.ChangeOfPostEvent;
import com.ll.goohaeyou.global.event.notification.CreateChatRoomEvent;
import com.ll.goohaeyou.global.event.notification.PostEmployedEvent;
import com.ll.goohaeyou.global.exception.GoohaeyouException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.ll.goohaeyou.domain.notification.entity.type.CauseTypeCode.APPLICATION_APPROVED;
import static com.ll.goohaeyou.domain.notification.entity.type.CauseTypeCode.APPLICATION_UNAPPROVE;
import static com.ll.goohaeyou.domain.notification.entity.type.ResultTypeCode.DELETE;
import static com.ll.goohaeyou.domain.notification.entity.type.ResultTypeCode.NOTICE;
import static com.ll.goohaeyou.global.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployService {
    private final JobPostService jobPostService;
    private final ApplicationEventPublisher publisher;

    public List<ApplicationDto> getList(String username, Long postId) {
        JobPostDetail postDetail = jobPostService.findByJobPostAndNameAndValidate(postId);
        checkPermissions(username,postDetail.getAuthor());

        return ApplicationDto.convertToDtoList(postDetail.getApplications());
    }

    @Transactional
    public void approve(String username, Long postId, List<Long> applicationIds) {
        JobPost jobPost = jobPostService.findByIdAndValidate(postId);
        JobPostDetail postDetail = jobPost.getJobPostDetail();
        Long postWriterId = jobPost.getMember().getId();

        WageStatus updateWageStatus = determineWageStatus(postDetail.getWage().getWagePaymentMethod());

        if (!jobPost.isClosed()) throw new GoohaeyouException(NOT_POSSIBLE_TO_APPROVE_IT_YET);
        checkPermissions(username,postDetail.getAuthor());

        List<Application> applicationList = new ArrayList<>();

        for (Application application : postDetail.getApplications()) {
            if(applicationIds.contains(application.getId())) {
                Long receiverId = application.getMember().getId();
                application.approve();

                application.updateWageStatus(updateWageStatus);
                application.setJobEndDate(jobPost.getJobStartDate()
                        .plusDays(postDetail.getWage().getWorkDays() - 1));

                increaseApplicantTransactionCount(application);
                increaseAuthorTransactionCount(jobPost);

                publisher.publishEvent(new ChangeOfPostEvent(this, jobPost, application,APPLICATION_APPROVED, NOTICE));
                publisher.publishEvent(new CreateChatRoomEvent(this,postWriterId,receiverId,jobPost.getTitle()));

            } else {
                application.reject();
                applicationList.add(application);
                publisher.publishEvent(new ChangeOfPostEvent(this, jobPost, application,APPLICATION_UNAPPROVE, DELETE));
            }
        }
        for (Application application : applicationList) {
            postDetail.getApplications().remove(application);
        }
        publisher.publishEvent(new PostEmployedEvent(this, jobPost));
    }

    public void checkPermissions (String username, String author){
        if (!username.equals(author)) throw new GoohaeyouException(NOT_ABLE);
    }

    private WageStatus determineWageStatus(WagePaymentMethod wagePaymentMethod) {
        return switch (wagePaymentMethod) {
            case SERVICE_PAYMENT -> WageStatus.PAYMENT_PENDING;
            case INDIVIDUAL_PAYMENT -> WageStatus.APPLICATION_APPROVED;
            default -> throw new GoohaeyouException(INVALID_WAGE_PAYMENT_METHOD);
        };
    }

    private void increaseApplicantTransactionCount(Application application) {
        if (isValidApplication(application)) {
            application.getMember().increaseTransactionCount();
        }
    }

    private void increaseAuthorTransactionCount(JobPost jobPost) {
        if (isValidJobPost(jobPost)) {
            jobPost.getMember().increaseTransactionCount();
        }
    }

    private boolean isValidApplication(Application application) {
        return application != null && application.getMember() != null;
    }

    private boolean isValidJobPost(JobPost jobPost) {
        return jobPost != null && jobPost.getMember() != null;
    }

}
