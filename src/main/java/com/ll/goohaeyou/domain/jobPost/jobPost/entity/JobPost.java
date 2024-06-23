package com.ll.goohaeyou.domain.jobPost.jobPost.entity;

import com.ll.goohaeyou.domain.category.entity.Category;
import com.ll.goohaeyou.domain.member.member.entity.Member;
import com.ll.goohaeyou.global.jpa.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

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

    private LocalDate deadline;

    private LocalDate jobStartDate;

    @Column(nullable = false)
    private boolean employed;

    @OneToOne(mappedBy = "jobPost", cascade = CascadeType.ALL, orphanRemoval = true)
    private JobPostDetail jobPostDetail;

    private int regionCode;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    public void update(String title, LocalDate deadline, LocalDate jobStartDate, int regionCode) {
        if (title != null && !title.isBlank()) {
            this.title = title;
        }
        if (regionCode != 0) {
            this.regionCode = regionCode;
        }
        this.deadline = deadline;
        this.jobStartDate = jobStartDate;
    }

    public void SetDeadlineNull() {
        this.deadline = null;
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
