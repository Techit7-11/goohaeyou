package com.ll.gooHaeYu.domain.calculate.calculate;

import com.ll.gooHaeYu.domain.application.application.entity.Application;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ApplicationReader {

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    public ItemReader<Application> applicationReader() {
        JpaPagingItemReader<Application> reader = new JpaPagingItemReader<>();
        reader.setEntityManagerFactory(entityManagerFactory);
        reader.setQueryString("SELECT a FROM Application a WHERE a.receive = false AND a.jobCompleted = true AND a.wageStatus = 'SETTLEMENT_REQUESTED'");
        reader.setPageSize(10);
        return reader;
    }
}