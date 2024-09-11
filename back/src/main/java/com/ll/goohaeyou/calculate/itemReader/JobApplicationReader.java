package com.ll.goohaeyou.calculate.itemReader;

import com.ll.goohaeyou.jobApplication.domain.entity.JobApplication;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Map;

@Component
public class JobApplicationReader {
    @Autowired
    private EntityManagerFactory entityManagerFactory;

    public ItemReader<JobApplication> completedApplicationReader() {
        JpaPagingItemReader<JobApplication> reader = new JpaPagingItemReader<>();
        reader.setEntityManagerFactory(entityManagerFactory);
        reader.setQueryString("SELECT a FROM JobApplication a WHERE a.receive = false AND a.jobCompleted = true AND a.wageStatus = 'SETTLEMENT_REQUESTED'");
        reader.setPageSize(10);
        return reader;
    }

    public ItemReader<JobApplication> exceeded3DaysApplication () {
        System.out.println("exceeded_3DAys_Job");
        JpaPagingItemReader<JobApplication> reader = new JpaPagingItemReader<>();
        reader.setEntityManagerFactory(entityManagerFactory);
        reader.setQueryString("SELECT a FROM JobApplication a WHERE a.receive = false AND a.jobCompleted IS NULL AND (a.wageStatus = 'PAYMENT_COMPLETED' OR a.wageStatus = 'APPLICATION_APPROVED') AND a.jobEndDate < :date");
        reader.setParameterValues(Map.of("date", LocalDate.now().minusDays(3)));
        reader.setPageSize(10);
        return reader;
    }
}