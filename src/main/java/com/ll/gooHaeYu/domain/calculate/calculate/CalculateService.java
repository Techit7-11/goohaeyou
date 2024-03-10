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
                .<Payment, Payment>chunk(10,ptm)
                .reader(paymentReader())
                .processor(paymentProcessor())
                .writer(paymentWriter())
                .build();
    }

    @Bean
    public ItemReader<Payment> paymentReader() {
        JpaPagingItemReader<Payment> reader = new JpaPagingItemReader<>();
        reader.setEntityManagerFactory(entityManagerFactory);
        reader.setQueryString("SELECT p FROM Payment p WHERE p.applicationId IN (SELECT a.id FROM Application a WHERE a.depositStatus = 'SETTLEMENT_REQUESTED') AND p.paid = true AND p.canceled = false");
        reader.setPageSize(10);
        return reader;
    }

    @Bean
    public ItemProcessor<Payment, Payment> paymentProcessor() {
        // 처리할 작업이 없으므로 null을 반환하거나 아이템을 그대로 반환
        return Payment -> Payment;
    }

    @Bean
    public ItemWriter<Payment> paymentWriter() {
        // 아이템을 출력하거나 처리하는 작업을 수행
        return items -> {
            for (Payment payment : items) {

                System.out.println("Processed Payment : " + payment.getId());
            }
        };
    }
}
