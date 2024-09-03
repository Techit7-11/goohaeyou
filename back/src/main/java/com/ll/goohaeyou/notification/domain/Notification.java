package com.ll.goohaeyou.notification.domain;

import com.ll.goohaeyou.member.member.domain.Member;
import com.ll.goohaeyou.notification.domain.type.CauseTypeCode;
import com.ll.goohaeyou.notification.domain.type.ResultTypeCode;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    private Notification(
            Member toMember,
            Member fromMember,
            String jobPostTitle,
            CauseTypeCode causeTypeCode,
            ResultTypeCode resultTypeCode,
            String url
    ) {
        this.createAt = LocalDateTime.now();
        this.toMember = toMember;
        this.fromMember = fromMember;
        this.relPostTitle = jobPostTitle;
        this.causeTypeCode = causeTypeCode;
        this.resultTypeCode = resultTypeCode;
        this.url = url;
    }

    public static Notification create(
            Member toMember,
            Member fromMember,
            String jobPostTitle,
            CauseTypeCode causeTypeCode,
            ResultTypeCode resultTypeCode,
            String url
    ) {
        return new Notification(
                toMember,
                fromMember,
                jobPostTitle,
                causeTypeCode,
                resultTypeCode,
                url
        );
    }

    public void update() {
        this.seen = true;
    }
}
