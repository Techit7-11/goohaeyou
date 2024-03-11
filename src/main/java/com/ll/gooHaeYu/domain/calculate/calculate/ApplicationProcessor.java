package com.ll.gooHaeYu.domain.calculate.calculate;

import com.ll.gooHaeYu.domain.application.application.entity.Application;
import com.ll.gooHaeYu.domain.jobPost.jobPost.entity.Wage;
import com.ll.gooHaeYu.domain.jobPost.jobPost.repository.WageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Transactional
@RequiredArgsConstructor
public class ApplicationProcessor implements ItemProcessor<Application, Application> {

    private final WageRepository wageRepository;

    @Override
    public Application process(Application application) throws Exception {
        System.out.println("------BEFORE------");
        System.out.println("Application.ID : " + application.getId());
        System.out.println("Application.DEPOSIT : " + application.getDepositStatus());
        System.out.println("Application.EARN : " + application.getEarn());
        System.out.println("Application.JOB_COMPLETED : " + application.getJobCompleted());
        System.out.println("Application.RECEIVE : " + application.isReceive());
        System.out.println("------BEFORE------");



        // Application에서 jobPostDetail 필드를 이용하여 WAGE 테이블에서 해당 jobPostDetail에 대한 데이터 추출
        Wage wage = wageRepository.findByJobPostDetail(application.getJobPostDetail());

        // WAGE 테이블에서 해당 jobPostDetail에 대한 데이터를 찾지 못한 경우
        if (wage == null) {
            return null; // 처리할 데이터 없음
        }

        // WAGE 테이블에서 cost 필드와 application의 earn 필드를 비교하여 동일한 경우
        if (wage.getCost() == application.getEarn()) {
            // application 객체의 Member의 restCash 필드에 application 객체의 earn 필드 값 전달
            application.getMember().setRestCash(application.getEarn());
            // application 객체의 earn 필드 값 0으로 변경
            application.setEarn(0);
            // application 객체의 receive 값을 true로 설정
            application.setReceive(true);
        }

        return application;
    }
}