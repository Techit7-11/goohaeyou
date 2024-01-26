package com.ll.gooHaeYu.global.init;

import com.ll.gooHaeYu.domain.jobPost.jobPost.dto.JobPostForm;
import com.ll.gooHaeYu.domain.jobPost.jobPost.service.JobPostService;
import com.ll.gooHaeYu.domain.member.member.dto.JoinForm;
import com.ll.gooHaeYu.domain.member.member.dto.LoginForm;
import com.ll.gooHaeYu.domain.member.member.repository.MemberRepository;
import com.ll.gooHaeYu.domain.member.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.IntStream;

@Configuration
@Slf4j
@RequiredArgsConstructor
@Profile("dev")
public class NotProd {
    private final MemberService memberService;
    private final JobPostService jobPostService;
    private final MemberRepository memberRepository;

    @Bean
    @Order(2)
    public ApplicationRunner initNotProd() {
        return new ApplicationRunner() {

            @Transactional
            @Override
            public void run(ApplicationArguments args) {
                if (memberRepository.findById(1L).isPresent()) return;

                JoinForm joinForm = JoinForm.builder()
                        .username("testUser1")
                        .password("11112222")
                        .build();

                memberService.join(joinForm);

                LoginForm loginForm = LoginForm.builder()
                        .username("testUser1")
                        .password("11112222")
                        .build();

                memberService.login(loginForm);

                IntStream.rangeClosed(1, 50).forEach(i -> {
                    JobPostForm.Register postRegister = JobPostForm.Register.builder()
                            .title("제목" + i)
                            .body("내용" + i)
                            .build();

                    jobPostService.writePost("testUser1", postRegister);
                });

            }
        };
    }
}
