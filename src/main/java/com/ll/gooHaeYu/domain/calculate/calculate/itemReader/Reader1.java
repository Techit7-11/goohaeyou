package com.ll.gooHaeYu.domain.calculate.calculate.itemReader;

import com.ll.gooHaeYu.domain.application.application.entity.Application;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TemporalType;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class Reader1 {

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    public ItemReader<Application> reader1() {
        JpaPagingItemReader<Application> reader = new JpaPagingItemReader<>();
        reader.setEntityManagerFactory(entityManagerFactory);
        reader.setQueryString("SELECT a FROM Application a WHERE a.receive = false AND a.jobCompleted IS NULL AND a.wageStatus = 'PAYMENT_COMPLETED' AND a.jobEndDate < :date");
        reader.setParameterValues(Map.of("date", LocalDate.now().minusDays(3)));
        reader.setPageSize(10);
        return reader;
    }
}