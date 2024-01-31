package com.ll.gooHaeYu.domain.jobPost.jobPost.entity;

import com.ll.gooHaeYu.domain.member.member.entity.Member;
import com.ll.gooHaeYu.global.jpa.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

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
    private long commentsCount;

    @Setter(PROTECTED)
    private long applicationCount;

    @Setter(PROTECTED)
    private long interestsCount;

    private String title;

    @Column(nullable = false)
    private boolean closed = false;

    @OneToOne(mappedBy = "jobPost", cascade = CascadeType.ALL, orphanRemoval = true)
    private JobPostDetail jobPostDetail;

    public void update(String title, Boolean closed) {
        if (title != null && !title.isBlank()) {
            this.title = title;
        }
        if (closed != null) {
            this.closed = closed;
        }
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
}
