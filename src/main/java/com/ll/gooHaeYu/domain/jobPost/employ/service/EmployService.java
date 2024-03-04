package com.ll.gooHaeYu.domain.jobPost.employ.service;

import com.ll.gooHaeYu.domain.application.application.dto.ApplicationDto;
import com.ll.gooHaeYu.domain.application.application.entity.Application;
import com.ll.gooHaeYu.domain.jobPost.jobPost.entity.JobPost;
import com.ll.gooHaeYu.domain.jobPost.jobPost.entity.JobPostDetail;
import com.ll.gooHaeYu.domain.jobPost.jobPost.service.JobPostService;
import com.ll.gooHaeYu.domain.member.member.entity.Member;
import com.ll.gooHaeYu.domain.member.member.service.MemberService;
import com.ll.gooHaeYu.global.event.ChangeOfPostEvent;
import com.ll.gooHaeYu.global.event.PostEmployedEvent;
import com.ll.gooHaeYu.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.ll.gooHaeYu.domain.notification.entity.type.CauseTypeCode.APPLICATION_APPROVED;
import static com.ll.gooHaeYu.domain.notification.entity.type.CauseTypeCode.APPLICATION_UNAPPROVE;
import static com.ll.gooHaeYu.domain.notification.entity.type.ResultTypeCode.DELETE;
import static com.ll.gooHaeYu.domain.notification.entity.type.ResultTypeCode.NOTICE;
import static com.ll.gooHaeYu.global.exception.ErrorCode.NOT_ABLE;
import static com.ll.gooHaeYu.global.exception.ErrorCode.NOT_POSSIBLE_TO_APPROVE_IT_YET;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployService {

    private final JobPostService jobPostService;
    private final ApplicationEventPublisher publisher;

    public List<ApplicationDto> getList(String username, Long postId) {
        JobPostDetail postDetail = jobPostService.findByJobPostAndNameAndValidate(postId);
        checkPermissions(username,postDetail.getAuthor());

        return ApplicationDto.toDtoList(postDetail.getApplications());
    }

    @Transactional
    public void approve(String username, Long postId, List<Long> applicationIds) {
        JobPost jobPost = jobPostService.findByIdAndValidate(postId);
        JobPostDetail postDetail = jobPost.getJobPostDetail();

        if (!jobPost.isClosed()) throw new CustomException(NOT_POSSIBLE_TO_APPROVE_IT_YET);
        checkPermissions(username,postDetail.getAuthor());

        List<Application> applicationList = new ArrayList<>();

        for (Application application : postDetail.getApplications()) {
            if(applicationIds.contains(application.getId())) {
                // TODO : 승인 후 알림
                application.approve();


                increaseApplicantTransactionCount(application);

                increaseAuthorTransactionCount(jobPost);


                publisher.publishEvent(new ChangeOfPostEvent(this, jobPost, application,APPLICATION_APPROVED, NOTICE));
            }else {
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
        if (!username.equals(author)) throw new CustomException(NOT_ABLE);
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
