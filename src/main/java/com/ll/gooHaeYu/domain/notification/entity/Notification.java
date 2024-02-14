package com.ll.gooHaeYu.domain.notification.entity;

import com.ll.gooHaeYu.domain.jobPost.jobPost.entity.JobPost;
import com.ll.gooHaeYu.domain.member.member.entity.Member;
import com.ll.gooHaeYu.domain.notification.entity.type.CauseTypeCode;
import com.ll.gooHaeYu.domain.notification.entity.type.ResultTypeCode;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
@Table(name = "notification")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime createAt;
    @ManyToOne
    @ToString.Exclude
    private Member toMember;
    @ManyToOne
    @ToString.Exclude
    private Member fromMember;
    @ToString.Exclude
    private String relPostTitle;
    @Enumerated(EnumType.STRING)
    private CauseTypeCode causeTypeCode;
    @Enumerated(EnumType.STRING)
    private ResultTypeCode resultTypeCode;
    private boolean seen;
    private String url;

    public void update() {
        this.seen = true;
    }
}
