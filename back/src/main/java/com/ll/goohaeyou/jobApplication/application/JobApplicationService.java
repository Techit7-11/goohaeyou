package com.ll.goohaeyou.jobApplication.application;

import com.ll.goohaeyou.global.event.notification.JobApplicationCreateAndChangedEvent;
import com.ll.goohaeyou.global.exception.EntityNotFoundException;
import com.ll.goohaeyou.jobApplication.application.dto.JobApplicationDetailResponse;
import com.ll.goohaeyou.jobApplication.application.dto.ModifyJobApplicationRequest;
import com.ll.goohaeyou.jobApplication.application.dto.MyJobApplicationResponse;
import com.ll.goohaeyou.jobApplication.application.dto.WriteJobApplicationRequest;
import com.ll.goohaeyou.jobApplication.domain.entity.JobApplication;
import com.ll.goohaeyou.jobApplication.domain.repository.JobApplicationRepository;
import com.ll.goohaeyou.jobApplication.policy.JobApplicationPolicy;
import com.ll.goohaeyou.jobPost.jobPost.domain.entity.JobPostDetail;
import com.ll.goohaeyou.jobPost.jobPost.domain.repository.JobPostDetailRepository;
import com.ll.goohaeyou.member.member.domain.entity.Member;
import com.ll.goohaeyou.member.member.domain.repository.MemberRepository;
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
    private final MemberRepository memberRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final ApplicationEventPublisher publisher;
    private final JobPostDetailRepository jobPostDetailRepository;
    private final JobApplicationPolicy jobApplicationPolicy;

    @Transactional
    public void writeApplication(String username, Long jobPostId, WriteJobApplicationRequest request) {
        JobPostDetail postDetail = jobPostDetailRepository.findById(jobPostId)
                .orElseThrow(EntityNotFoundException.PostNotExistsException::new);
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(EntityNotFoundException.MemberNotFoundException::new);

        jobApplicationPolicy.validateCanWrite(postDetail, member);

        JobApplication newJobApplication = createNewApplication(member, postDetail, request);

        postDetail.getJobApplications().add(newJobApplication);
        postDetail.getJobPost().increaseApplicationsCount();
        jobApplicationRepository.save(newJobApplication);

        publisher.publishEvent(new JobApplicationCreateAndChangedEvent(this, postDetail, newJobApplication, APPLICATION_CREATED));
    }

    private JobApplication createNewApplication(Member member, JobPostDetail postDetail, WriteJobApplicationRequest request) {
        return JobApplication.create(member, postDetail, request.body());
    }

    public JobApplicationDetailResponse getDetailById(Long id) {
        JobApplication jobApplication = findByIdAndValidate(id);

        return JobApplicationDetailResponse.from(jobApplication);
    }

    public JobApplication findByIdAndValidate(Long id) {
        return jobApplicationRepository.findById(id)
                .orElseThrow(EntityNotFoundException.JobApplicationNotExistsException::new);
    }

    @Transactional
    public void modifyJobApplication(String username, Long id, ModifyJobApplicationRequest request) {
        JobApplication jobApplication = findByIdAndValidate(id);

        jobApplicationPolicy.validateCanModify(username, jobApplication);

        jobApplication.updateBody(request.body());
        publisher.publishEvent(new JobApplicationCreateAndChangedEvent(this, jobApplication, APPLICATION_MODIFICATION));
    }

    @Transactional
    public void deleteJobApplication(String username, Long id) {
        JobApplication jobApplication = findByIdAndValidate(id);

        jobApplicationPolicy.validateCanDelete(username, jobApplication);

        jobApplication.getJobPostDetail().getJobPost().decreaseApplicationsCount();
        jobApplicationRepository.deleteById(id);
    }

    public List<MyJobApplicationResponse> findByUsername(String username) {

        Member member = memberRepository.findByUsername(username)
                .orElseThrow(EntityNotFoundException.MemberNotFoundException::new);

        return MyJobApplicationResponse.convertToList(jobApplicationRepository.findByMemberId(member.getId()));
    }
}
