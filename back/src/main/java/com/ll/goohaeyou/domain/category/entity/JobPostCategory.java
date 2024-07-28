package com.ll.goohaeyou.domain.category.entity;

import com.ll.goohaeyou.domain.jobPost.jobPost.entity.JobPost;
import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@AllArgsConstructor(access = PROTECTED)
@NoArgsConstructor(access = PROTECTED)
@Builder
@Getter
public class JobPostCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "job_post_id", nullable = false)
    private JobPost jobPost;

    public void updateCategory(Category category) {
        this.category = category;
    }
}
