package com.ll.goohaeyou.calculate.itemReader;

import com.ll.goohaeyou.application.domain.Application;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Map;

@Component
public class ApplicationReader {
    @Autowired
    private EntityManagerFactory entityManagerFactory;

    public ItemReader<Application> completedApplicationReader() {
        JpaPagingItemReader<Application> reader = new JpaPagingItemReader<>();
        reader.setEntityManagerFactory(entityManagerFactory);
        reader.setQueryString("SELECT a FROM Application a WHERE a.receive = false AND a.jobCompleted = true AND a.wageStatus = 'SETTLEMENT_REQUESTED'");
        reader.setPageSize(10);
        return reader;
    }

    public ItemReader<Application> exceeded3DaysApplication () {
        System.out.println("exceeded_3DAys_Job");
        JpaPagingItemReader<Application> reader = new JpaPagingItemReader<>();
        reader.setEntityManagerFactory(entityManagerFactory);
        reader.setQueryString("SELECT a FROM Application a WHERE a.receive = false AND a.jobCompleted IS NULL AND (a.wageStatus = 'PAYMENT_COMPLETED' OR a.wageStatus = 'APPLICATION_APPROVED') AND a.jobEndDate < :date");
        reader.setParameterValues(Map.of("date", LocalDate.now().minusDays(3)));
        reader.setPageSize(10);
        return reader;
    }
}