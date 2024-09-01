package com.ll.goohaeyou.jobPost.jobPost.domain;

import com.ll.goohaeyou.member.member.domain.type.Gender;
import jakarta.persistence.*;
import lombok.*;

import static lombok.AccessLevel.PROTECTED;

@Entity
@AllArgsConstructor(access = PROTECTED)
@NoArgsConstructor(access = PROTECTED)
@Getter
@Builder
@Table(name = "essential")
public class Essential {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int minAge;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Gender gender = Gender.UNDEFINED;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_post_detail_id", nullable = false)
    private JobPostDetail jobPostDetail;

    public void update(int minAge, Gender gender) {
        this.minAge = minAge;
        if (gender!=null) {
            this.gender = gender;
        }
    }
}