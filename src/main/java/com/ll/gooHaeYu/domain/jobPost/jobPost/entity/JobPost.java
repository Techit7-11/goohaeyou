package com.ll.gooHaeYu.domain.jobPost.jobPost.entity;

import com.ll.gooHaeYu.domain.category.entity.Category;
import com.ll.gooHaeYu.domain.member.member.entity.Member;
import com.ll.gooHaeYu.global.jpa.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

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

    private String title;

    private String body;

    @Column(nullable = false)
    private boolean isClosed = false;

    public void upData(String title, String body, boolean isClosed) {
        this.title = title;
        this.body = body;
        this.isClosed = isClosed;
    }
}
