package com.ll.goohaeyou.jobApplication.application;

import com.ll.goohaeyou.global.event.notification.JobApplicationCreateAndChangedEvent;
import com.ll.goohaeyou.jobApplication.application.dto.JobApplicationDetailResponse;
import com.ll.goohaeyou.jobApplication.application.dto.ModifyJobApplicationRequest;
import com.ll.goohaeyou.jobApplication.application.dto.MyJobApplicationResponse;
import com.ll.goohaeyou.jobApplication.application.dto.WriteJobApplicationRequest;
import com.ll.goohaeyou.jobApplication.domain.JobApplicationDomainService;
import com.ll.goohaeyou.jobApplication.domain.entity.JobApplication;
import com.ll.goohaeyou.jobApplication.policy.JobApplicationPolicy;
import com.ll.goohaeyou.jobPost.jobPost.domain.JobPostDetailDomainService;
import com.ll.goohaeyou.jobPost.jobPost.domain.entity.JobPostDetail;
import com.ll.goohaeyou.member.member.domain.MemberDomainService;
import com.ll.goohaeyou.member.member.domain.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.ll.goohaeyou.notification.domain.type.CauseTypeCode.APPLICATION_CREATED;
import static com.ll.goohaeyou.notification.domain.type.CauseTypeCode.APPLICATION_MODIFICATION;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JobApplicationService {
    private final ApplicationEventPublisher publisher;
    private final JobApplicationPolicy jobApplicationPolicy;
    private final MemberDomainService memberDomainService;
    private final JobPostDetailDomainService jobPostDetailDomainService;
    private final JobApplicationDomainService jobApplicationDomainService;

    @Transactional
    public void writeApplication(String username, Long jobPostId, WriteJobApplicationRequest request) {
        JobPostDetail postDetail = jobPostDetailDomainService.getById(jobPostId);
        Member member = memberDomainService.getMemberByUsername(username);

        jobApplicationPolicy.validateCanWrite(postDetail, member);

        JobApplication newJobApplication = jobApplicationDomainService.create(member, postDetail, request);

        jobPostDetailDomainService.addJobApplication(postDetail, newJobApplication);

        publisher.publishEvent(new JobApplicationCreateAndChangedEvent(this, postDetail, newJobApplication, APPLICATION_CREATED));
    }

    public JobApplicationDetailResponse getDetailById(Long id) {
        JobApplication jobApplication = jobApplicationDomainService.getById(id);

        return JobApplicationDetailResponse.from(jobApplication);
    }

    @Transactional
    public void modifyJobApplication(String username, Long id, ModifyJobApplicationRequest request) {
        JobApplication jobApplication = jobApplicationDomainService.getById(id);

        jobApplicationPolicy.validateCanModify(username, jobApplication);

        jobApplication.updateBody(request.body());
        publisher.publishEvent(new JobApplicationCreateAndChangedEvent(this, jobApplication, APPLICATION_MODIFICATION));
    }

    @Transactional
    public void deleteJobApplication(String username, Long id) {
        JobApplication jobApplication = jobApplicationDomainService.getById(id);

        jobApplicationPolicy.validateCanDelete(username, jobApplication);

        jobApplication.getJobPostDetail().getJobPost().decreaseApplicationsCount();

        jobApplicationDomainService.deleteById(id);
    }

    public List<MyJobApplicationResponse> findByUsername(String username) {

        Member member = memberDomainService.getMemberByUsername(username);

        return MyJobApplicationResponse.convertToList(jobApplicationDomainService.getByMemberId(member.getId()));
    }
}
