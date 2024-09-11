package com.ll.goohaeyou.jobPost.jobPost.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Entity
@NoArgsConstructor(access = PROTECTED)
@Getter
@Table(name = "job_post_image")
public class JobPostImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String jobPostImageUrl;

    @Column(nullable = false)
    private boolean isMainImage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_post_detail_id", nullable = false)
    private JobPostDetail jobPostDetail;

    private JobPostImage(
            String jobPostImageUrl,
            boolean isMainImage,
            JobPostDetail jobPostDetail
    ) {
        this.jobPostImageUrl = jobPostImageUrl;
        this.isMainImage = isMainImage;
        this.jobPostDetail = jobPostDetail;
    }

    public static JobPostImage create(
            String jobPostImageUrl,
            boolean isMainImage,
            JobPostDetail jobPostDetail
    ) {
        return new JobPostImage(
                jobPostImageUrl,
                isMainImage,
                jobPostDetail
        );
    }

    public void setMainImage() {
        this.isMainImage = true;
    }

    public void unsetMainImage() {
        this.isMainImage = false;
    }
}
