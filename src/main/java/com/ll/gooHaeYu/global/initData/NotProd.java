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
                        .location("경기 수원시 영통구 이의동 263-1")
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

                JobPostForm.Register postRegister1 = JobPostForm.Register.builder()
                        .title("주말 세탁물정리알바구해요 초보가능")
                        .body("세탁물 단순 개비기입니다.\n" +
                                "초보자도 가능해요.")
                        .location("부산 동구 중앙대로 539")
                        .deadLine(LocalDate.now().plusWeeks(2))
                        .build();

                jobPostService.writePost("testUser1", postRegister1);

                JobPostForm.Register postRegister2 = JobPostForm.Register.builder()
                        .title("과일 포장해 주실 분 구합니다")
                        .body("5kg 과일박스 개인작업대로 들어올려 택배포장작업 또는 기타 포장작업 하는 업무 입니다 \n" +
                                "개인작업대에서 과일 택배포장하시는거고 시간 잘갑니다\n" +
                                "\n" +
                                "0201 목요일 하루\n" +
                                "\n" +
                                "시간 8:00~12:00\n" +
                                "일끝나고 입금")
                        .location("경남 창원시 진해구 신항10로 98")
                        .deadLine(LocalDate.now().plusWeeks(2))
                        .minAge(20)
                        .gender(Gender.UNDEFINED)
                        .build();

                jobPostService.writePost("testUser1", postRegister2);

                JobPostForm.Register postRegister3 = JobPostForm.Register.builder()
                        .title("촬영 현장 현장보조알바 구인 ")
                        .body("유튜브 예능 촬영 현장 보조알바 분을 모집합니다.\n" +
                                "약속 펑크 안내시는 책임감 있는 분들과 함께 일하게되는 기회가 되었으면 합니다.\n" +
                                "\n" +
                                "촬영 일정 : 02/10 14:30~22:00 / 이동 시간이 포함된 시간입니다.\n" +
                                "\n" +
                                "집합지 : 서울 성수역 인근\n" +
                                "\n" +
                                "담당 업무 : 촬영진행 보조\n" +
                                "\n" +
                                "페이 : 10만원\n" +
                                "\n" +
                                "\n" +
                                "사무실에서 함께 짐을 챙겨 이동하고 촬영종료 후 정리하는 것이 주업무입니다.\n" +
                                "\n" +
                                "현장 스탭 안내에 따라 도움주시면 됩니다 :)\n")
                        .location("서울 성동구 아차산로 100")
                        .deadLine(LocalDate.now().plusDays(5))
                        .minAge(20)
                        .gender(Gender.MALE)
                        .build();

                jobPostService.writePost("testUser1", postRegister3);

                IntStream.rangeClosed(1, 50).forEach(i -> {
                    JobPostForm.Register postRegister = JobPostForm.Register.builder()
                            .title("구인공고 제목" + i)
                            .body("구인공고 내용" + i)
                            .location("서울특별시 광진구 천호대로124길")
                            .deadLine(LocalDate.now().plusWeeks(2))
                            .minAge(20)
                            .build();

                    jobPostService.writePost("testUser1", postRegister);
                });

            }
        };
    }
}
