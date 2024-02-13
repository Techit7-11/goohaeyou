package com.ll.gooHaeYu.domain.jobPost.jobPost.service;

import com.ll.gooHaeYu.domain.jobPost.jobPost.entity.JobPost;
import com.ll.gooHaeYu.global.event.PostDeadlineEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JobPostBatchService {
    private final JobPostService jobPostService;
    private final ApplicationEventPublisher publisher;

    @Transactional
    @Scheduled(cron = "0 0 0 * * *") // 00:00:00.000000에 실행
//    @Scheduled(cron = "0 */1 * * * *") // 매 1분마다 실행
    public void checkAndCloseExpiredJobPosts() {
        List<JobPost> expiredJobPosts = jobPostService.findExpiredJobPosts(LocalDate.now());
        for (JobPost jobPost : expiredJobPosts) {
            publisher.publishEvent(new PostDeadlineEvent(this, jobPost));

//        LocalDateTime
//        List<JobPost> expiredJobPosts = jobPostService.findExpiredJobPosts(LocalDateTime.now());
//        for (JobPost jobPost : expiredJobPosts) {
//            publisher.publishEvent(new PostDeadlineEvent(this, jobPost));
        }
    }
}
