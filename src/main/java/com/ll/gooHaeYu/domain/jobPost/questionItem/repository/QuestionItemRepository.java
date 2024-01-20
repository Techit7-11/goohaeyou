package com.ll.gooHaeYu.domain.jobPost.questionItem.repository;

import com.ll.gooHaeYu.domain.jobPost.questionItem.entity.QuestionItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionItemRepository extends JpaRepository<QuestionItem, Long> {
}
