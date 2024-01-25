package com.ll.gooHaeYu.domain.application.application.repository;

import com.ll.gooHaeYu.domain.application.application.entity.ResponseItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResponseItemRepository extends JpaRepository<ResponseItem, Long> {
}
