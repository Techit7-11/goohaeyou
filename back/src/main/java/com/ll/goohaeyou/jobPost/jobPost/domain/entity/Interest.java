package com.ll.goohaeyou.jobPost.jobPost.domain.entity;

import jakarta.persistence.*;
import com.ll.goohaeyou.member.member.domain.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "interest")
public class Interest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "job_post_detail_id")
    private JobPostDetail jobPostDetail;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private Interest(
            JobPostDetail jobPostDetail,
            Member member
    ) {
        this.jobPostDetail = jobPostDetail;
        this.member = member;
    }

    public static Interest create(
            JobPostDetail jobPostDetail,
            Member member) {
        return new Interest(
                jobPostDetail,
                member
        );
    }
}
