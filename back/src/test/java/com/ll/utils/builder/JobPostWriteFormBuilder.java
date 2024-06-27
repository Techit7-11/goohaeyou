package com.ll.utils.builder;

import com.ll.goohaeyou.domain.jobPost.jobPost.dto.JobPostForm;
import com.ll.goohaeyou.domain.jobPost.jobPost.entity.type.PayBasis;
import com.ll.goohaeyou.domain.jobPost.jobPost.entity.type.WagePaymentMethod;
import com.ll.goohaeyou.domain.member.member.entity.type.Gender;

import java.time.LocalDate;

public class JobPostWriteFormBuilder {
    private String title = "공고 제목";
    private String body = "공고 내용";
    private String location = "경기 성남시 분당구 판교역로 4";
    private int minAge = 20;
    private Gender gender = Gender.UNDEFINED;
    private LocalDate deadLine = LocalDate.now().plusDays(3);
    private LocalDate jobStartDate = LocalDate.now().plusDays(5);
    private int workTime = 8;
    private int workDays = 5; // 원래 값이 1이었으나, 예시 변경을 위해 5로 설정
    private PayBasis payBasis = PayBasis.TOTAL_HOURS;
    private int cost = 1000000;
    private WagePaymentMethod wagePaymentMethod = WagePaymentMethod.INDIVIDUAL_PAYMENT;

    public JobPostWriteFormBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    public JobPostWriteFormBuilder setBody(String body) {
        this.body = body;
        return this;
    }

    public JobPostWriteFormBuilder setLocation(String location) {
        this.location = location;
        return this;
    }

    public JobPostWriteFormBuilder setMinAge(int minAge) {
        this.minAge = minAge;
        return this;
    }

    public JobPostWriteFormBuilder setGender(Gender gender) {
        this.gender = gender;
        return this;
    }

    public JobPostWriteFormBuilder setDeadLine(LocalDate deadLine) {
        this.deadLine = deadLine;
        return this;
    }

    public JobPostWriteFormBuilder setJobStartDate(LocalDate jobStartDate) {
        this.jobStartDate = jobStartDate;
        return this;
    }

    public JobPostWriteFormBuilder setWorkTime(int workTime) {
        this.workTime = workTime;
        return this;
    }

    public JobPostWriteFormBuilder setWorkDays(int workDays) {
        this.workDays = workDays;
        return this;
    }

    public JobPostWriteFormBuilder setPayBasis(PayBasis payBasis) {
        this.payBasis = payBasis;
        return this;
    }

    public JobPostWriteFormBuilder setCost(int cost) {
        this.cost = cost;
        return this;
    }

    public JobPostWriteFormBuilder setWagePaymentMethod(WagePaymentMethod wagePaymentMethod) {
        this.wagePaymentMethod = wagePaymentMethod;
        return this;
    }

    public JobPostForm.Register build() {
        return new JobPostForm.Register(title, body, location, minAge, gender, deadLine, jobStartDate, workTime, workDays, payBasis, cost, wagePaymentMethod);
    }
}
