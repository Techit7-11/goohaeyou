package com.ll.goohaeyou.jobApplication.application;

import com.ll.goohaeyou.auth.exception.AuthException;
import com.ll.goohaeyou.global.event.notification.JobApplicationCreateAndChangedEvent;
import com.ll.goohaeyou.global.exception.EntityNotFoundException;
import com.ll.goohaeyou.jobApplication.application.dto.JobApplicationDto;
import com.ll.goohaeyou.jobApplication.application.dto.JobApplicationForm;
import com.ll.goohaeyou.jobApplication.domain.JobApplication;
import com.ll.goohaeyou.jobApplication.domain.repository.JobApplicationRepository;
import com.ll.goohaeyou.jobApplication.exception.JobApplicationException;
import com.ll.goohaeyou.jobPost.jobPost.domain.JobPostDetail;
import com.ll.goohaeyou.jobPost.jobPost.domain.repository.JobPostDetailRepository;
import com.ll.goohaeyou.member.member.domain.Member;
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

    @Transactional
    public void writeApplication(String username, Long jobPostId, JobApplicationForm.Register form) {
        JobPostDetail postDetail = jobPostDetailRepository.findById(jobPostId)
                .orElseThrow(EntityNotFoundException.PostNotExistsException::new);
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(EntityNotFoundException.MemberNotFoundException::new);
        canWrite(postDetail, member);

        JobApplication newJobApplication = createNewApplication(member, postDetail, form);

        postDetail.getJobApplications().add(newJobApplication);
        postDetail.getJobPost().increaseApplicationsCount();
        jobApplicationRepository.save(newJobApplication);

        publisher.publishEvent(new JobApplicationCreateAndChangedEvent(this, postDetail, newJobApplication, APPLICATION_CREATED));
    }

    private JobApplication createNewApplication(Member member, JobPostDetail postDetail, JobApplicationForm.Register form) {
        return JobApplication.create(member, postDetail, form.getBody());
    }

    public JobApplicationDto findById(Long id) {
        JobApplication jobApplication = findByIdAndValidate(id);

        return JobApplicationDto.from(jobApplication);
    }

    public JobApplication findByIdAndValidate(Long id) {
        return jobApplicationRepository.findById(id)
                .orElseThrow(EntityNotFoundException.JobApplicationNotExistsException::new);
    }

    @Transactional
    public void modifyJobApplication(String username, Long id, JobApplicationForm.Modify form) {
        JobApplication jobApplication = findByIdAndValidate(id);

        if (!isJobApplicationAuthor(username, jobApplication.getMember().getUsername())) {
            throw new AuthException.NotAuthorizedException();
        }

        jobApplication.updateBody(form.getBody());
        publisher.publishEvent(new JobApplicationCreateAndChangedEvent(this, jobApplication, APPLICATION_MODIFICATION));
    }

    public boolean isJobApplicationAuthor(String username, String author) {
        return username.equals(author);
    }

    @Transactional
    public void deleteJobApplication(String username, Long id) {
        JobApplication jobApplication = findByIdAndValidate(id);

        canDelete(username, jobApplication);

        jobApplication.getJobPostDetail().getJobPost().decreaseApplicationsCount();
        jobApplicationRepository.deleteById(id);
    }

    public boolean canDelete(String username, JobApplication jobApplication) {
        if (jobApplication.getApprove()) {
            throw new AuthException.NotAuthorizedException();
        }

        if (!isJobApplicationAuthor(username, jobApplication.getMember().getUsername()))
            throw new AuthException.NotAuthorizedException();

        return true;
    }

    public List<JobApplicationDto> findByUsername(String username) {

        Member member = memberRepository.findByUsername(username)
                .orElseThrow(EntityNotFoundException.MemberNotFoundException::new);

        return JobApplicationDto.convertToDtoList(jobApplicationRepository.findByMemberId(member.getId()));
    }

    private void canWrite(JobPostDetail postDetail, Member member) {
        if (postDetail.getJobPost().isClosed()){ // 공고 지원 마감
            throw new JobApplicationException.ClosedPostExceptionJob();
        }

        if (postDetail.getAuthor().equals(member.getUsername())) { // 자신의 공고에 지원 불가능
            throw new JobApplicationException.NotEligibleForOwnJobExceptionJob();
        }

        for (JobApplication jobApplication : postDetail.getJobApplications()) { // 지원서 중복 불가능
            if (jobApplication.getMember().equals(member)) {
                throw new JobApplicationException.DuplicateSubmissionExceptionJob();
            }
        }
    }
}
