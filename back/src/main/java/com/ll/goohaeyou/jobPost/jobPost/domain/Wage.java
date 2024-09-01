package com.ll.goohaeyou.jobPost.jobPost.domain;

import com.ll.goohaeyou.jobPost.jobPost.domain.type.PayBasis;
import com.ll.goohaeyou.jobPost.jobPost.domain.type.WagePaymentMethod;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Entity
@AllArgsConstructor(access = PROTECTED)
@NoArgsConstructor(access = PROTECTED)
@Builder
@Getter
@Table(name = "wage")
public class Wage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int cost;

    private boolean paid;

    private int workTime;

    private int workDays;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private PayBasis payBasis = PayBasis.UNDEFINED;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private WagePaymentMethod wagePaymentMethod = WagePaymentMethod.UNDEFINED;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_post_detail_id", nullable = false)
    private JobPostDetail jobPostDetail;

    public void updateWageInfo(int cost, PayBasis payBasis, int workTime, int workDays) {
        this.cost = cost;
        this.payBasis = payBasis;
        this.workTime = workTime;
        this.workDays = workDays;
    }
}
