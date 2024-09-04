package com.ll.goohaeyou.calculate.itemWriter;

import com.ll.goohaeyou.jobApplication.domain.JobApplication;
import jakarta.persistence.EntityManager;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JobApplicationWriter implements ItemWriter<JobApplication> {
    @Autowired
    private EntityManager entityManager;

    @Override
    public void write(Chunk<? extends JobApplication> items) throws Exception {
        for (JobApplication jobApplication : items) {
            entityManager.merge(jobApplication);
        }
    }
}