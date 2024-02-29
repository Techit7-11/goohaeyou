package com.ll.gooHaeYu.domain.member.review.entity;

import com.ll.gooHaeYu.domain.jobPost.jobPost.entity.JobPost;
import com.ll.gooHaeYu.domain.member.member.entity.Member;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

@Entity
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "review")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String body;

    @Min(1)
    @Max(5)
    @Column(nullable = false)
    private double score;

    @ManyToOne
    @JoinColumn(name = "job_posting_id", nullable = false)
    private JobPost jobPost;

    @ManyToOne
    @JoinColumn(name = "applicant_id", nullable = false)
    private Member applicant;

    @ManyToOne
    @JoinColumn(name = "recruiter_id", nullable = false)
    private Member recruiter;
}
