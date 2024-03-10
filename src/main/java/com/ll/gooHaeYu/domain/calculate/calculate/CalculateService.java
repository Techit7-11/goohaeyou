package com.ll.gooHaeYu.domain.calculate.calculate;

import com.ll.gooHaeYu.domain.application.application.entity.Application;
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

    @Autowired
    private EntityManagerFactory entityManagerFactory;

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
                .reader(applicationReader())
                .processor(applicationProcessor())
                .writer(applicationWriter())
                .build();
    }

    @Bean
    public ItemReader<Application> applicationReader() {
        JpaPagingItemReader<Application> reader = new JpaPagingItemReader<>();
        reader.setEntityManagerFactory(entityManagerFactory);
        reader.setQueryString("SELECT a FROM Application a WHERE a.depositStatus = 'APPLICATION_APPROVED'");
        reader.setPageSize(1); // 페이지 크기 설정
        return reader;
    }

    @Bean
    public ItemProcessor<Application, Application> applicationProcessor() {
        // 처리할 작업이 없으므로 null을 반환하거나 아이템을 그대로 반환
        return application -> application;
    }

    @Bean
    public ItemWriter<Application> applicationWriter() {
        // 아이템을 출력하거나 처리하는 작업을 수행
        return items -> {
            for (Application application : items) {
                // UNDEFINED 값을 갖고있는 application의 Id 출력
                System.out.println("Processed application: " + application.getId());
            }
        };
    }
}
