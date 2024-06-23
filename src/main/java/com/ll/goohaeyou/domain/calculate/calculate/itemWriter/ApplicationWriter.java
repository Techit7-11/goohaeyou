package com.ll.goohaeyou.domain.calculate.calculate.itemWriter;

import com.ll.goohaeyou.domain.application.application.entity.Application;
import jakarta.persistence.EntityManager;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ApplicationWriter implements ItemWriter<Application> {

    @Autowired
    private EntityManager entityManager;

    @Override
    public void write(Chunk<? extends Application> items) throws Exception {
        for (Application application : items) {
            entityManager.merge(application);
        }
    }
}