package com.ll.goohaeyou.application.application;

import com.ll.goohaeyou.application.application.dto.ApplicationDto;
import com.ll.goohaeyou.application.application.dto.ApplicationForm;
import com.ll.goohaeyou.application.domain.repository.ApplicationRepository;
import com.ll.goohaeyou.application.domain.Application;
import com.ll.goohaeyou.application.domain.type.WageStatus;
import com.ll.goohaeyou.jobPost.jobPost.domain.JobPostDetail;
import com.ll.goohaeyou.jobPost.jobPost.application.JobPostService;
import com.ll.goohaeyou.member.member.domain.Member;
import com.ll.goohaeyou.member.member.application.MemberService;
import com.ll.goohaeyou.global.event.notification.ApplicationCreateAndChangedEvent;
import com.ll.goohaeyou.application.exception.ApplicationException;
import com.ll.goohaeyou.auth.exception.AuthException;
import com.ll.goohaeyou.jobPost.jobPost.exception.JobPostException;
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
public class ApplicationService {
    private final MemberService memberService;
    private final JobPostService jobPostService;
    private final ApplicationRepository applicationRepository;
    private final ApplicationEventPublisher publisher;

    @Transactional
    public void writeApplication(String username, Long id, ApplicationForm.Register form) {
        JobPostDetail postDetail = jobPostService.findByJobPostAndNameAndValidate(id);
        Member member = memberService.getMember(username);
        canWrite(postDetail, member);

        Application newApplication = createNewApplication(member, postDetail, form);

        postDetail.getApplications().add(newApplication);
        postDetail.getJobPost().increaseApplicationsCount();
        applicationRepository.save(newApplication);

        publisher.publishEvent(new ApplicationCreateAndChangedEvent(this, postDetail, newApplication, APPLICATION_CREATED));
    }

    private Application createNewApplication(Member member, JobPostDetail postDetail, ApplicationForm.Register form) {
        return Application.builder()
                .member(member)
                .jobPostDetail(postDetail)
                .body(form.getBody())
                .approve(null)
                .wageStatus(WageStatus.UNDEFINED)
                .build();
    }

    public ApplicationDto findById(Long id) {
        Application application = findByIdAndValidate(id);

        return ApplicationDto.from(application);
    }

    public Application findByIdAndValidate(Long id) {
        return applicationRepository.findById(id)
                .orElseThrow(JobPostException.PostNotExistsException::new);
    }

    @Transactional
    public void modifyApplication(String username, Long id, ApplicationForm.Modify form) {
        Application application = findByIdAndValidate(id);

        if (!isApplicationAuthor(username, application.getMember().getUsername())) {
            throw new AuthException.NotAuthorizedException();
        }

        application.updateBody(form.getBody());
        publisher.publishEvent(new ApplicationCreateAndChangedEvent(this, application, APPLICATION_MODIFICATION));
    }

    public boolean isApplicationAuthor(String username, String author) {
        return username.equals(author);
    }

    @Transactional
    public void deleteApplication(String username, Long id) {
        Application application = findByIdAndValidate(id);

        canDelete(username, application);

        application.getJobPostDetail().getJobPost().decreaseApplicationsCount();
        applicationRepository.deleteById(id);
    }

    public boolean canDelete(String username, Application application) {
        if (application.getApprove()) {
            throw new AuthException.NotAuthorizedException();
        }

        if (!isApplicationAuthor(username, application.getMember().getUsername()))
            throw new AuthException.NotAuthorizedException();

        return true;
    }

    public List<ApplicationDto> findByUsername(String username) {

        Member member = memberService.getMember(username);

        return ApplicationDto.convertToDtoList(applicationRepository.findByMemberId(member.getId()));
    }

    private void canWrite(JobPostDetail postDetail, Member member) {
        if (postDetail.getJobPost().isClosed()){ // 공고 지원 마감
            throw new ApplicationException.ClosedPostException();
        }

        if (postDetail.getAuthor().equals(member.getUsername())) { // 자신의 공고에 지원 불가능
            throw new ApplicationException.NotEligibleForOwnJobException();
        }

        for (Application application : postDetail.getApplications()) { // 지원서 중복 불가능
            if (application.getMember().equals(member)) {
                throw new ApplicationException.DuplicateSubmissionException();
            }
        }
    }

    @Transactional
    public void updateApplicationOnPaymentSuccess(Long applicationId, Long amount) {
        Application application = findByIdAndValidate(applicationId);
        application.updateWageStatus(WageStatus.PAYMENT_COMPLETED);
        application.setEarn(Math.toIntExact(amount));
    }
}
