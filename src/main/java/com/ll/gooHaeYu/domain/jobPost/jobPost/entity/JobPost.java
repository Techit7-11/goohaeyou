package com.ll.gooHaeYu.domain.jobPost.jobPost.entity;

import com.ll.gooHaeYu.domain.member.member.entity.Member;
import com.ll.gooHaeYu.global.jpa.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static lombok.AccessLevel.PROTECTED;

@Entity
@AllArgsConstructor(access = PROTECTED)
@NoArgsConstructor(access = PROTECTED)
@Builder
@Getter
@Table(name = "job_post")
public class JobPost extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Setter(PROTECTED)
    private long commentsCount = 0;

    @Setter(PROTECTED)
    private long applicationCount = 0;

    @Setter(PROTECTED)
    private long interestsCount = 0;

    @Setter(PROTECTED)
    private long incrementViewCount = 0;

    private String title;

    private String location;

    @Column(nullable = false)
    private boolean closed = false;

    @Column(nullable = false)
    private LocalDate deadline;

    @Column(nullable = false)
    private boolean employed;

    @OneToOne(mappedBy = "jobPost", cascade = CascadeType.ALL, orphanRemoval = true)
    private JobPostDetail jobPostDetail;

    public void update(String title,LocalDate deadline) {
        if (title != null && !title.isBlank()) {
            this.title = title;
        }
        this.deadline = deadline;
    }

    public void close() {
        this.closed = true;
    }

    public void employed() {
        this.employed = true;
    }

    public void increaseCommentsCount() {
        commentsCount++;
    }

    public void decreaseCommentsCount() {
        commentsCount--;
    }

    public void increaseApplicationsCount() {
        applicationCount++;
    }

    public void decreaseApplicationsCount() {
        applicationCount--;
    }

    public void increaseInterestCount() {
        interestsCount++;
    }

    public void decreaseInterestCount() {
        interestsCount--;
    }

    public void increaseViewCount() {
        this.incrementViewCount++;
    }
}
