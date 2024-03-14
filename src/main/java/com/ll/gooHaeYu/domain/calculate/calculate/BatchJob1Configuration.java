package com.ll.gooHaeYu.domain.calculate.calculate;

import com.ll.gooHaeYu.domain.application.application.entity.Application;
import com.ll.gooHaeYu.domain.calculate.calculate.itemProcessor.ApplicationProcessor;
import com.ll.gooHaeYu.domain.calculate.calculate.itemProcessor.Processor1;
import com.ll.gooHaeYu.domain.calculate.calculate.itemReader.ApplicationReader;
import com.ll.gooHaeYu.domain.calculate.calculate.itemReader.Reader1;
import com.ll.gooHaeYu.domain.calculate.calculate.itemWriter.ApplicationWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class BatchJob1Configuration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager ptm;
    private final JobListener jobListener;
    private final ApplicationWriter applicationWriter;
    private final Reader1 reader1;
    private final Processor1 processor1;

    @Bean(name = "batchJob1")
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
                .reader(reader1.reader1())
                .processor(processor1)
                .writer(applicationWriter)
                .build();
    }
}
