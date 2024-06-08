package com.ll.gooHaeYu.domain.jobPost.jobPost.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Entity
@AllArgsConstructor(access = PROTECTED)
@NoArgsConstructor(access = PROTECTED)
@Getter
@Builder
@Table(name = "job_post_image")
public class JobPostImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String jobPostImageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_post_detail_id", nullable = false)
    private JobPostDetail jobPostDetail;
}
