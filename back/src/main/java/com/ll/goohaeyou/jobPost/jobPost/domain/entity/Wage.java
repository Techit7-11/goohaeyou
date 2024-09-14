package com.ll.goohaeyou.jobPost.jobPost.domain.entity;

import com.ll.goohaeyou.jobPost.jobPost.domain.type.PayBasis;
import com.ll.goohaeyou.jobPost.jobPost.domain.type.WagePaymentMethod;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Entity
@NoArgsConstructor(access = PROTECTED)
@Getter
@Table(name = "wage")
public class Wage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int cost;
    private int workTime;
    private int workDays;
    private boolean paid;

    @Enumerated(EnumType.STRING)
    private PayBasis payBasis;

    @Enumerated(EnumType.STRING)
    private WagePaymentMethod wagePaymentMethod;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_post_detail_id", nullable = false)
    private JobPostDetail jobPostDetail;

    private Wage(
            int cost,
            int workTime,
            int workDays,
            PayBasis payBasis,
            WagePaymentMethod wagePaymentMethod,
            JobPostDetail jobPostDetail
    ) {
        this.cost = cost;
        this.workTime = workTime;
        this.workDays = workDays;
        this.payBasis = payBasis;
        this.wagePaymentMethod = wagePaymentMethod;
        this.jobPostDetail = jobPostDetail;
    }

    public static Wage create(
            int cost,
            Integer workTime,
            Integer workDays,
            PayBasis payBasis,
            WagePaymentMethod wagePaymentMethod,
            JobPostDetail jobPostDetail
    ) {
        int resolveWorkTime = (workTime != null) ? workTime : 0;
        int resolveWorkDays = (workDays != null) ? workDays : 0;

        return new Wage(
                cost,
                resolveWorkTime,
                resolveWorkDays,
                payBasis,
                wagePaymentMethod,
                jobPostDetail
        );
    }

    public void updateWageInfo(int cost, PayBasis payBasis, int workTime, int workDays) {
        this.cost = cost;
        this.payBasis = payBasis;
        this.workTime = workTime;
        this.workDays = workDays;
    }
}
