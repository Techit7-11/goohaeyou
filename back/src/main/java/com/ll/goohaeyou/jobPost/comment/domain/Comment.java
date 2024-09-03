package com.ll.goohaeyou.jobPost.comment.domain;

import com.ll.goohaeyou.jobPost.jobPost.domain.JobPostDetail;
import com.ll.goohaeyou.member.member.domain.Member;
import com.ll.goohaeyou.global.jpa.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "comment")
public class Comment extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_post_detail_id", nullable = false)
    private JobPostDetail jobPostDetail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    private String content;

    private Comment(
            JobPostDetail jobPostDetail,
            Member member,
            String content
    ) {
        this.jobPostDetail = jobPostDetail;
        this.member = member;
        this.content = content;
    }

    public static Comment create(
            JobPostDetail jobPostDetail,
            Member member,
            String content
    ) {
        return new Comment(
                jobPostDetail,
                member,
                content
        );
    }

    public void update(String content) {
        if (content != null && !content.isBlank()) {
            this.content = content;
        }
    }
}
