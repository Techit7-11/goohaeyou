package com.ll.gooHaeYu.domain.calculate.calculate.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JobScheduler {

    private final JobLauncher jobLauncher;
    @Qualifier("calculateJob")
    private final Job calculateJob;

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
}
//      첫 날:
//            00:00: runJob() 실행
//            06:00: runJobEvery6Hours() 실행
//            12:00: runJobEvery6Hours() 실행
//            18:00: runJobEvery6Hours() 실행
//    둘째 날:
//            00:00: runJobEvery6Hours() 실행
//            06:00: runJobEvery6Hours() 실행
//            12:00: runJobEvery6Hours() 실행
//            18:00: runJobEvery6Hours() 실행
//    셋째 날:
//            00:00: runJobEvery6Hours() 실행
//            06:00: runJobEvery6Hours() 실행 ...