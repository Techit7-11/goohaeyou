package com.ll.gooHaeYu.domain.calculate.calculate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BatchController {

    private final Job job;
    private final JobLauncher jobLauncher;

    @PostMapping("/batch")
    // 개발과정에서 확인을 위해 작성
    public ResponseEntity<String> runBatch() {
        try {
            JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
            jobParametersBuilder.addLong("time", System.currentTimeMillis()); // 시간을 파라미터로 전달
            JobExecution jobExecution = jobLauncher.run(job, jobParametersBuilder.toJobParameters());
            return ResponseEntity.ok("Batch job started with ID: " + jobExecution.getId());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to start batch job: " + e.getMessage());
        }
    }

}