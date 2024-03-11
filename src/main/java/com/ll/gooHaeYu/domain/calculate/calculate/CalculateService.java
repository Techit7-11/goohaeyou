package com.ll.gooHaeYu.domain.calculate.calculate;

import com.ll.gooHaeYu.domain.application.application.entity.Application;
import com.ll.gooHaeYu.domain.payment.payment.entity.Payment;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class CalculateService {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager ptm;
    private final JobListener jobListener;
    private final ApplicationReader applicationReader;
    private final ApplicationProcessor applicationProcessor;
    private final ApplicationWriter applicationWriter;

    @Bean
    public Job batchJob1() {
        return new JobBuilder("batchJob1", jobRepository)
                .start(step1())
                .listener(jobListener)
                .build();

    }

    @Bean
    public Step step1() {
        return new StepBuilder("step1", jobRepository)
                .<Application, Application>chunk(10,ptm)
                .reader(applicationReader.applicationReader())
                .processor(applicationProcessor)
                .writer(applicationWriter)
                .build();
    }

}
