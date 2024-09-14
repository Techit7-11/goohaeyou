package com.ll.goohaeyou.calculate.configuration;

import com.ll.goohaeyou.jobApplication.domain.entity.JobApplication;
import com.ll.goohaeyou.calculate.itemReader.JobApplicationReader;
import com.ll.goohaeyou.calculate.JobListener;
import com.ll.goohaeyou.calculate.itemProcessor.Exceeded3DaysProcessor;
import com.ll.goohaeyou.calculate.itemWriter.JobApplicationWriter;
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
public class Exceeded3DaysConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager ptm;
    private final JobListener jobListener;
    private final JobApplicationWriter jobApplicationWriter;
    private final JobApplicationReader jobApplicationReader;
    private final Exceeded3DaysProcessor processor1;

    @Bean(name = "exceeded3DaysJob")
    public Job batchJob1() {
        return new JobBuilder("exceeded3DaysJob", jobRepository)
                .start(step1())
                .listener(jobListener)
                .build();
    }


    @Bean
    public Step step1() {
        return new StepBuilder("exceeded3DaysStep1", jobRepository)
                .<JobApplication, JobApplication>chunk(10,ptm)
                .reader(jobApplicationReader.exceeded3DaysApplication())
                .processor(processor1)
                .writer(jobApplicationWriter)
                .build();
    }
}
