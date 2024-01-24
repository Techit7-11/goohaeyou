package com.ll.gooHaeYu.domain.jobPost.jobPost.entity;

import com.ll.gooHaeYu.domain.category.entity.Category;
import com.ll.gooHaeYu.domain.member.member.entity.Member;
import com.ll.gooHaeYu.global.jpa.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "job_post_id")
    private List<QuestionItem> questionItems = new ArrayList<>();

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
}
