package com.ll.goohaeyou.calculate.configuration;

import com.ll.goohaeyou.jobApplication.domain.JobApplication;
import com.ll.goohaeyou.calculate.itemProcessor.JobApplicationProcessor;
import com.ll.goohaeyou.calculate.itemReader.JobApplicationReader;
import com.ll.goohaeyou.calculate.itemWriter.JobApplicationWriter;
import com.ll.goohaeyou.calculate.JobListener;
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
    private final JobApplicationReader jobApplicationReader;
    private final JobApplicationProcessor jobApplicationProcessor;
    private final JobApplicationWriter jobApplicationWriter;

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
                .<JobApplication, JobApplication>chunk(10,ptm)
                .reader(jobApplicationReader.completedApplicationReader())
                .processor(jobApplicationProcessor)
                .writer(jobApplicationWriter)
                .build();
    }


}
