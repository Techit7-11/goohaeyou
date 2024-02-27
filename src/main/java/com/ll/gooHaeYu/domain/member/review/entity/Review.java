package com.ll.gooHaeYu.domain.member.review.entity;

import com.ll.gooHaeYu.domain.jobPost.jobPost.entity.JobPost;
import com.ll.gooHaeYu.domain.member.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
@Table(name = "review")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String body;

    private double score;

    private Long job_posting_id;

    @ManyToOne
    @JoinColumn(name = "job_posting_id", nullable = false)
    private JobPost jobPost;

    @ManyToOne
    @JoinColumn(name = "apllicant_id", nullable = false)
    private Member applicant_id;

    @ManyToOne
    @JoinColumn(name = "recruiter_id", nullable = false)
    private Member recruiter_id;
}

