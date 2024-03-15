package com.ll.gooHaeYu.domain.calculate.calculate.configuration;

import com.ll.gooHaeYu.domain.application.application.entity.Application;
import com.ll.gooHaeYu.domain.calculate.calculate.JobListener;
import com.ll.gooHaeYu.domain.calculate.calculate.itemProcessor.ApplicationProcessor;
import com.ll.gooHaeYu.domain.calculate.calculate.itemReader.ApplicationReader;
import com.ll.gooHaeYu.domain.calculate.calculate.itemWriter.ApplicationWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class CalculateConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager ptm;
    private final JobListener jobListener;
    private final ApplicationReader applicationReader;
    private final ApplicationProcessor applicationProcessor;
    private final ApplicationWriter applicationWriter;

    @Bean(name ="calculateJob" )
    public Job calculateJob() {
        return new JobBuilder("calculateJob", jobRepository)
                .start(calculateStep1())
                .listener(jobListener)
                .build();

    }

    @JobScope
    @Bean
    public Step calculateStep1() {
        return new StepBuilder("calculateStep1", jobRepository)
                .<Application, Application>chunk(10,ptm)
                .reader(applicationReader.completedApplicationReader())
                .processor(applicationProcessor)
                .writer(applicationWriter)
                .build();
    }


}
