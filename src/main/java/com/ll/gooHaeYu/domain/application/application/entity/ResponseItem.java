package com.ll.gooHaeYu.domain.application.application.entity;

import com.ll.gooHaeYu.domain.jobPost.jobPost.entity.QuestionItem;
import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
@Table(name = "response_item")
public class ResponseItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_item_id", nullable = false)
    private QuestionItem questionItem;

    private String content;
}
