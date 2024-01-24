package com.ll.gooHaeYu.domain.jobPost.jobPost.service;

import com.ll.gooHaeYu.domain.jobPost.jobPost.dto.QuestionItemForm;
import com.ll.gooHaeYu.domain.jobPost.jobPost.entity.JobPost;
import com.ll.gooHaeYu.domain.jobPost.jobPost.entity.QuestionItem;
import com.ll.gooHaeYu.domain.jobPost.jobPost.repository.QuestionItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuestionItemService {
    private final QuestionItemRepository questionItemRepository;

    public QuestionItem createQuestionItem(QuestionItemForm form, JobPost jobPost) {
        return QuestionItem.builder()
                .jobPost(jobPost)
                .content(form.getContent())
                .build();
    }

    @Transactional
    public void saveQuestionItems(List<QuestionItem> questionItems) {
        questionItemRepository.saveAll(questionItems);
    }
}
