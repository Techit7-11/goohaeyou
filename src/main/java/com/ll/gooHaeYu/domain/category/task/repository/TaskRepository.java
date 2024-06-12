package com.ll.gooHaeYu.domain.category.task.repository;

import com.ll.gooHaeYu.domain.category.task.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
}
