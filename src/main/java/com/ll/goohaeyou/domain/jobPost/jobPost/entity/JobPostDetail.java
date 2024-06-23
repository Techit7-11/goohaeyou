package com.ll.goohaeyou.domain.jobPost.jobPost.entity;

import com.ll.goohaeyou.domain.application.application.entity.Application;
import com.ll.goohaeyou.domain.jobPost.comment.entity.Comment;
import com.ll.goohaeyou.global.jpa.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@AllArgsConstructor(access = PROTECTED)
@NoArgsConstructor(access = PROTECTED)
@Builder
@Getter
@Table(name = "job_post_detail",uniqueConstraints = {@UniqueConstraint(columnNames = {"jobPost_id","author"})})
public class JobPostDetail extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "job_post_id", nullable = false)
    private JobPost jobPost;

    private String author;

    @Column(columnDefinition = "TEXT")
    private String body;

    @OneToOne(mappedBy = "jobPostDetail", cascade = ALL, orphanRemoval = true)
    private Essential essential;

    @OneToOne(mappedBy = "jobPostDetail", cascade = ALL)
    private Wage wage;

    @OneToMany(mappedBy = "jobPostDetail", cascade = ALL, orphanRemoval = true)
    @ToString.Exclude
    @Builder.Default
    @OrderBy("id DESC")
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "jobPostDetail", cascade = ALL, orphanRemoval = true)
    @ToString.Exclude
    @Builder.Default
    private List<Application> applications = new ArrayList<>();

    @OneToMany(mappedBy = "jobPostDetail",cascade = ALL, orphanRemoval = true)
    @ToString.Exclude
    @Builder.Default
    private List<Interest> interests = new ArrayList<>();

    @OneToMany(mappedBy = "jobPostDetail",cascade = ALL, orphanRemoval = true)
    @ToString.Exclude
    @Builder.Default
    private List<JobPostImage> jobPostImages = new ArrayList<>();

    public void updatePostDetail(String body) {
        if (body != null && !body.isBlank()) {
            this.body = body;
        }
    }
}
