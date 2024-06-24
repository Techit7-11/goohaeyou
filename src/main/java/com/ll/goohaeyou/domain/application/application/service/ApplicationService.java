package com.ll.goohaeyou.domain.application.application.service;

import com.ll.goohaeyou.domain.application.application.dto.ApplicationDto;
import com.ll.goohaeyou.domain.application.application.dto.ApplicationForm;
import com.ll.goohaeyou.domain.application.application.entity.Application;
import com.ll.goohaeyou.domain.application.application.entity.type.WageStatus;
import com.ll.goohaeyou.domain.application.application.entity.repository.ApplicationRepository;
import com.ll.goohaeyou.domain.jobPost.jobPost.entity.JobPostDetail;
import com.ll.goohaeyou.domain.jobPost.jobPost.service.JobPostService;
import com.ll.goohaeyou.domain.member.member.entity.Member;
import com.ll.goohaeyou.domain.member.member.service.MemberService;
import com.ll.goohaeyou.global.event.notification.ApplicationCreateAndChangedEvent;
import com.ll.goohaeyou.global.exception.CustomException;
import com.ll.goohaeyou.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.ll.goohaeyou.domain.notification.entity.type.CauseTypeCode.APPLICATION_CREATED;
import static com.ll.goohaeyou.domain.notification.entity.type.CauseTypeCode.APPLICATION_MODIFICATION;
import static com.ll.goohaeyou.global.exception.ErrorCode.NOT_ABLE;
import static com.ll.goohaeyou.global.exception.ErrorCode.POST_NOT_EXIST;

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

        return ApplicationDto.fromEntity(application);
    }

    public Application findByIdAndValidate(Long id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new CustomException(POST_NOT_EXIST));
    }

    @Transactional
    public void modifyApplication(String username, Long id, ApplicationForm.Modify form) {
        Application application = findByIdAndValidate(id);

        if (!isApplicationAuthor(username, application.getMember().getUsername()))
            throw new CustomException(NOT_ABLE);

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
            throw new CustomException(NOT_ABLE);
        }
        if (!isApplicationAuthor(username, application.getMember().getUsername()))
            throw new CustomException(NOT_ABLE);

        return true;
    }

    public List<ApplicationDto> findByUsername(String username) {

        Member member = memberService.getMember(username);

        return ApplicationDto.toDtoList(applicationRepository.findByMemberId(member.getId()));
    }

    private void canWrite(JobPostDetail postDetail, Member member) {
        if (postDetail.getJobPost().isClosed()){ // 공고 지원 마감
            throw new CustomException(ErrorCode.CLOSED_POST);
        }

        if (postDetail.getAuthor().equals(member.getUsername())) { // 자신의 공고에 지원 불가능
            throw new CustomException(ErrorCode.NOT_ELIGIBLE_FOR_OWN_JOB);
        }

        for (Application application : postDetail.getApplications()) { // 지원서 중복 불가능
            if (application.getMember().equals(member)) {
                throw new CustomException(ErrorCode.DUPLICATE_SUBMISSION);
            }
        }
    }

    @Transactional
    public void updateApplicationOnPaymentSuccess(Long applicationId, Long amount) {
        Application application = findByIdAndValidate(applicationId);
        application.updateWageStatus(WageStatus.PAYMENT_COMPLETED);
        application.setEarn(Math.toIntExact(amount));    // 지원서의 earn에 급여 추가
    }
}
