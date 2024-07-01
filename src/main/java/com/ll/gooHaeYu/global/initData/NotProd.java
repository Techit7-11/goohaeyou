package com.ll.gooHaeYu.global.initData;

import com.ll.gooHaeYu.domain.jobPost.jobPost.repository.JobPostRepository;
import com.ll.gooHaeYu.domain.jobPost.jobPost.service.JobPostService;
import com.ll.gooHaeYu.domain.member.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@Slf4j
@RequiredArgsConstructor
@Profile("!prod")
public class NotProd {
    private final MemberService memberService;
    private final JobPostService jobPostService;
    private final JobPostRepository jobPostRepository;

    @Value("${app.init-run}")
    private boolean initRun;   // application-dev.yml 에서 app: init-run: true 로 설정 하면 샘플 데이터 생성 (기본값: false)

    @Bean
    @Order(3)
    public ApplicationRunner initNotProd() {
        return new ApplicationRunner() {

            @Transactional
            @Override
            public void run(ApplicationArguments args) {
                if (!initRun) return;
                if (jobPostRepository.findById(1L).isPresent()) return;

            }
        };
    }
}
