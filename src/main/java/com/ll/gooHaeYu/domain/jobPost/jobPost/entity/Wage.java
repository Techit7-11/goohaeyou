package com.ll.gooHaeYu.domain.jobPost.jobPost.entity;

import com.ll.gooHaeYu.domain.jobPost.jobPost.entity.type.PayBasis;
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

    private String workTime;

    private int cost;

    private boolean paid;

    @Enumerated(EnumType.STRING)
    private PayBasis payBasis = PayBasis.UNDEFINED;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_post_detail", nullable = false)
    private JobPostDetail jobPostDetail;

    public void updateWageInfo(String workTime, int cost, PayBasis payBasis) {
        this.workTime = workTime;
        this.cost = cost;
        this.payBasis = payBasis;
    }
}
