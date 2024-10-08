package com.ll.goohaeyou.calculate.itemProcessor;

import com.ll.goohaeyou.global.event.cashLog.CashLogEvent;
import com.ll.goohaeyou.global.event.notification.CalculateNotificationEvent;
import com.ll.goohaeyou.jobApplication.domain.entity.JobApplication;
import com.ll.goohaeyou.jobPost.jobPost.domain.entity.Wage;
import com.ll.goohaeyou.jobPost.jobPost.domain.repository.WageRepository;
import com.ll.goohaeyou.member.member.domain.entity.Member;
import com.ll.goohaeyou.member.member.domain.repository.MemberRepository;
import com.ll.goohaeyou.payment.cashLog.domain.service.CashLogDomainService;
import com.ll.goohaeyou.payment.cashLog.domain.entity.CashLog;
import com.ll.goohaeyou.payment.payment.exception.PaymentException;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JobApplicationProcessor implements ItemProcessor<JobApplication, JobApplication> {
    private final WageRepository wageRepository;
    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher publisher;
    private final CashLogDomainService cashLogDomainService;

    @Override
    public JobApplication process(JobApplication jobApplication) throws Exception {
        Wage wage = wageRepository.findByJobPostDetail(jobApplication.getJobPostDetail());

        if (wage == null) {
            throw new RuntimeException("일치하는 WAGE 없음");
        }

        Member member = jobApplication.getMember();
        CashLog cashLog = cashLogDomainService.getByApplicationId(jobApplication.getId());
        int cost = (int)cashLog.getNetAmount();

        if (wage.getCost() == jobApplication.getEarn()) {
            member.addRestCash(cost);
            publisher.publishEvent(new CashLogEvent(this, jobApplication));
            jobApplication.updateEarn(0);
            jobApplication.markAsReceived(true);
            memberRepository.save(member);
            publisher.publishEvent(new CalculateNotificationEvent(this, jobApplication));
        } else throw new PaymentException.PaymentAmountMismatchException();
        return jobApplication;
    }
}