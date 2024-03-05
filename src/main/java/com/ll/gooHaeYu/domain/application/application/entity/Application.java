package com.ll.gooHaeYu.domain.application.application.entity;

import com.ll.gooHaeYu.domain.application.application.entity.type.DepositStatus;
import com.ll.gooHaeYu.domain.jobPost.jobPost.entity.JobPostDetail;
import com.ll.gooHaeYu.domain.member.member.entity.Member;
import com.ll.gooHaeYu.global.jpa.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
@Table(name = "application")
public class Application extends BaseTimeEntity {

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
    private DepositStatus depositStatus = DepositStatus.UNDEFINED;

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

    public void updateDepositStatus(DepositStatus newStatus) {
        if (newStatus != null) {
            this.depositStatus = newStatus;
        }
    }
}
