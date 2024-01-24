package com.ll.gooHaeYu.domain.jobPost.questionItem.dto;

import com.ll.gooHaeYu.domain.jobPost.questionItem.entity.QuestionItem;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QuestionItemDto {

    private Long id;
    private Long jobPostId;
    private String content;

    public static QuestionItemDto fromEntity(QuestionItem item) {
        return QuestionItemDto.builder()
                .id(item.getId())
                .jobPostId(item.getJobPost().getId())
                .content(item.getContent())
                .build();
    }
}
