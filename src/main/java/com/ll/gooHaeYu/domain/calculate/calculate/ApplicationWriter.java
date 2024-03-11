package com.ll.gooHaeYu.domain.calculate.calculate;

import com.ll.gooHaeYu.domain.application.application.entity.Application;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
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
            System.out.println("------AFTER------");
            System.out.println("Application.ID : " + application.getId());
            System.out.println("Application.DEPOSIT : " + application.getDepositStatus());
            System.out.println("Application.EARN : " + application.getEarn());
            System.out.println("Application.JOB_COMPLETED : " + application.getJobCompleted());
            System.out.println("Application.RECEIVE : " + application.isReceive());
            System.out.println("------AFTER------");

            entityManager.merge(application); // 엔티티 저장
        }
    }
}