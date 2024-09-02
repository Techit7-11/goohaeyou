package com.ll.goohaeyou.jobApplication.domain;

import com.ll.goohaeyou.jobApplication.domain.type.WageStatus;
import com.ll.goohaeyou.jobPost.jobPost.domain.JobPostDetail;
import com.ll.goohaeyou.member.member.domain.Member;
import com.ll.goohaeyou.global.jpa.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
@Table(name = "application")
public class JobApplication extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_post_detail_id", nullable = false)
    private JobPostDetail jobPostDetail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column
    private Boolean approve = null;

    private String body;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private WageStatus wageStatus = WageStatus.UNDEFINED;

    private int earn;

    private boolean receive;

    private Boolean jobCompleted;

    private LocalDate jobEndDate;

    public void updateBody(String body) {
        if (body != null && !body.isBlank()) {
            this.body = body;
        }
    }

    public void approve(){
        this.approve = true;
    }

    public void reject() {
        this.approve = false;
    }

    public void updateWageStatus(WageStatus newStatus) {
        if (newStatus != null) {
            this.wageStatus = newStatus;
        }
    }


    public void changeToCompleted() {
        this.jobCompleted = true;
    }

    public void changeToNotCompleted() {
        this.jobCompleted = false;
    }

    public void setEarn(int earn) {
        this.earn = earn;
    }

    public void setReceive(boolean receive) {
        this.receive = receive;
    }

    public void setJobEndDate(LocalDate jobEndDate) {
        this.jobEndDate = jobEndDate;
    }
}
