package com.ll.goohaeyou.review.domain.entity;

import com.ll.goohaeyou.jobPost.jobPost.domain.entity.JobPost;
import com.ll.goohaeyou.member.member.domain.entity.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
@Setter
@Table(name = "review")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String body;

    @Column(nullable = false)
    private double score;

    @ManyToOne
    @JoinColumn(name = "job_posting_id", nullable = false)
    private JobPost jobPostingId;

    @ManyToOne
    @JoinColumn(name = "applicant_id")
    private Member applicantId;

    @ManyToOne
    @JoinColumn(name = "recruiter_id")
    private Member recruiterId;
}
