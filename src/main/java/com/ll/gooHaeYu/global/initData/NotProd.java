package com.ll.gooHaeYu.global.initData;

import com.ll.gooHaeYu.domain.jobPost.jobPost.dto.JobPostForm;
import com.ll.gooHaeYu.domain.jobPost.jobPost.repository.JobPostRepository;
import com.ll.gooHaeYu.domain.jobPost.jobPost.service.JobPostService;
import com.ll.gooHaeYu.domain.member.member.dto.JoinForm;
import com.ll.gooHaeYu.domain.member.member.dto.LoginForm;
import com.ll.gooHaeYu.domain.member.member.entity.type.Gender;
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

import java.time.LocalDate;
import java.util.stream.IntStream;

@Configuration
@Slf4j
@RequiredArgsConstructor
@Profile("dev")
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

                JoinForm joinForm1 = JoinForm.builder()
                        .username("testUser1")
                        .password("11112222")
                        .birth(LocalDate.parse("2000-01-01"))
                        .gender(Gender.MALE)
                        .name("테스트")
                        .location("경기도 수원시 영통구 이의동 263-1")
                        .phoneNumber("01011112222")
                        .build();

                JoinForm joinForm2 = JoinForm.builder()
                        .username("admin")
                        .password("12345678")
                        .birth(LocalDate.parse("1995-01-01"))
                        .gender(Gender.FEMALE)
                        .name("관리자")
                        .location("서울특별시 중구 세종대로 110")
                        .phoneNumber("01033334444")
                        .build();

                memberService.join(joinForm1);
                memberService.join(joinForm2);

                LoginForm loginForm = LoginForm.builder()
                        .username("testUser1")
                        .password("11112222")
                        .build();

                memberService.login(loginForm);

                IntStream.rangeClosed(1, 50).forEach(i -> {
                    JobPostForm.Register postRegister = JobPostForm.Register.builder()
                            .title("구인공고 제목" + i)
                            .body("구인공고 내용" + i)
                            .location("서울특별시 광진구 천호대로124길")
                            .build();

                    jobPostService.writePost("testUser1", postRegister);
                });

            }
        };
    }
}
