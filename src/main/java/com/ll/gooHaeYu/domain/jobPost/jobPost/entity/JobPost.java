package com.ll.gooHaeYu.domain.jobPost.jobPost.entity;

import com.ll.gooHaeYu.domain.application.application.entity.Application;
import com.ll.gooHaeYu.domain.category.entity.Category;
import com.ll.gooHaeYu.domain.jobPost.comment.entity.Comment;
import com.ll.gooHaeYu.domain.member.member.entity.Member;
import com.ll.gooHaeYu.global.jpa.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.ALL;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "jobPost", cascade = ALL, orphanRemoval = true)
    @ToString.Exclude
    @Builder.Default
    @OrderBy("id DESC")
    private List<Comment> comments = new ArrayList<>();
    @Setter(PROTECTED)
    private long commentsCount;

    @OneToMany(mappedBy = "jobPost", cascade = ALL, orphanRemoval = true)
    @ToString.Exclude
    @Builder.Default
    private List<Application> applications = new ArrayList<>();
    @Setter(PROTECTED)
    private long applicationCount;

    @OneToMany(mappedBy = "jobPost",cascade = ALL, orphanRemoval = true)
    @ToString.Exclude
    @Builder.Default
    private List<Interest> interests = new ArrayList<>();
    @Setter(PROTECTED)
    private long interestsCount;

    private String title;

    private String body;

    @Column(nullable = false)
    private boolean closed = false;

    public void update(String title, String body, Boolean closed) {
        if (title != null && !title.isBlank()) {
            this.title = title;
        }
        if (body != null && !body.isBlank()) {
            this.body = body;
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
