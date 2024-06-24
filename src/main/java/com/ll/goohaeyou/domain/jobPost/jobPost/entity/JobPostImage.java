package com.ll.goohaeyou.domain.jobPost.jobPost.entity;

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

    @Column(nullable = false)
    private String jobPostImageUrl;

    @Column(nullable = false)
    private boolean isMainImage = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_post_detail_id", nullable = false)
    private JobPostDetail jobPostDetail;

    public void setMain() {
        this.isMainImage = true;
    }

    public void unsetMain() {
        this.isMainImage = false;
    }
}
