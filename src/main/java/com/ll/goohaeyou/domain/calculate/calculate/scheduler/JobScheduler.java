package com.ll.goohaeyou.domain.calculate.calculate.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JobScheduler {
    private final JobLauncher jobLauncher;
    private final Job calculateJob;
    private final Job exceeded3DaysJob;

    private boolean isFirstDay = true;

    // 매일 00시에 실행
    @Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Seoul")
//    @Scheduled(cron = "0 59 17 * * ?",  zone = "Asia/Seoul") 테스트
    public void runJob() throws Exception {
        if (isFirstDay) {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();
            jobLauncher.run(calculateJob, jobParameters);
            isFirstDay = false;
        }
    }

    // 6시간마다 실행
    @Scheduled(fixedDelay = 6 * 60 * 60 * 1000)
//    @Scheduled(fixedDelay = 60 * 1000) 1분마다 테스트
    public void runJobEvery6Hours() throws Exception {
        if (!isFirstDay) {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();
            jobLauncher.run(calculateJob, jobParameters);
        }
    }

        @Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Seoul")
//    @Scheduled(fixedRate = 5000) 5초마다 테스트
    public void exceeded3DaysJob() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();
        jobLauncher.run(exceeded3DaysJob, jobParameters);
    }
}