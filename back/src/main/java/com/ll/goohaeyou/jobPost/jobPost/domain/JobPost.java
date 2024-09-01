package com.ll.goohaeyou.jobPost.jobPost.domain;

import com.ll.goohaeyou.member.member.domain.Member;
import com.ll.goohaeyou.global.jpa.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Table(name = "job_post", indexes = {
        @Index(name = "idx_created_at", columnList = "created_at")
})
@AllArgsConstructor(access = PROTECTED)
@NoArgsConstructor(access = PROTECTED)
@Builder
@Getter
public class JobPost extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
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

    private LocalDate deadline;

    private LocalDate jobStartDate;

    @Column(nullable = false)
    private boolean employed;

    @OneToOne(mappedBy = "jobPost", cascade = CascadeType.ALL, orphanRemoval = true)
    private JobPostDetail jobPostDetail;

    private int regionCode;

    public void update(String title, LocalDate deadline, LocalDate jobStartDate, String location, int regionCode) {
        if (title != null && !title.isBlank()) {
            this.title = title;
        }
        this.location = location;
        this.regionCode = regionCode;
        this.deadline = deadline;
        this.jobStartDate = jobStartDate;
    }

    public void setDeadlineNull() {
        this.deadline = null;
    }

    public void close() {
        this.closed = true;
    }

    public void employed() {
        this.employed = true;
    }

    public void increaseCommentsCount() {
        this.commentsCount++;
    }

    public void decreaseCommentsCount() {
        if (commentsCount > 0) {
            this.commentsCount--;
        }
    }

    public void increaseApplicationsCount() {
        this.applicationCount++;
    }

    public void decreaseApplicationsCount() {
        if (this.applicationCount > 0) {
            this.applicationCount--;
        }
    }

    public void increaseInterestCount() {
        this.interestsCount++;
    }

    public void decreaseInterestCount() {
        if (interestsCount > 0) {
            this.interestsCount--;
        }
    }

    public void increaseViewCount() {
        this.incrementViewCount++;
    }
}
