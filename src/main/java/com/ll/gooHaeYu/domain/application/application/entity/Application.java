package com.ll.gooHaeYu.domain.application.application.entity;

import com.ll.gooHaeYu.domain.jobPost.jobPost.entity.JobPost;
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
@Table(name = "application")
public class Application extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_post_id", nullable = false)
    private JobPost jobPost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private boolean approve = false;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "application_id")
    private List<ResponseItem> responseItems = new ArrayList<>();
}
