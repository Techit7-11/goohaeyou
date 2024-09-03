package com.ll.goohaeyou.jobPost.jobPost.domain;

import com.ll.goohaeyou.member.member.domain.type.Gender;
import jakarta.persistence.*;
import lombok.*;

import static lombok.AccessLevel.PROTECTED;

@Entity
@NoArgsConstructor(access = PROTECTED)
@Getter
@Table(name = "essential")
public class Essential {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int minAge;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender = Gender.UNDEFINED;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_post_detail_id", nullable = false)
    private JobPostDetail jobPostDetail;

    private Essential(int minAge, Gender gender, JobPostDetail jobPostDetail) {
        this.minAge = minAge;
        this.gender = (gender != null) ? gender : Gender.UNDEFINED;
        this.jobPostDetail = jobPostDetail;
    }

    public static Essential create(Integer minAge, Gender gender, JobPostDetail jobPostDetail) {
        int resolvedMinAge = (minAge != null) ? minAge : 0;

        return new Essential(
                resolvedMinAge,
                gender,
                jobPostDetail
        );
    }

    public void update(int minAge, Gender gender) {
        this.minAge = minAge;
        if (gender!=null) {
            this.gender = gender;
        }
    }
}