//package com.ll.goohaeyou.controller.jobPost;
//
//import com.ll.goohaeyou.domain.jobPost.jobPost.service.JobPostService;
//import com.ll.goohaeyou.domain.member.member.dto.JoinForm;
//import com.ll.goohaeyou.domain.member.member.dto.LoginForm;
//import com.ll.goohaeyou.domain.member.member.dto.MemberForm;
//import com.ll.goohaeyou.domain.member.member.entity.Member;
//import com.ll.goohaeyou.domain.member.member.entity.type.Gender;
//import com.ll.goohaeyou.domain.member.member.service.MemberService;
//import com.ll.goohaeyou.auth.application.JwtTokenProvider;
//import com.ll.utils.builder.JobPostWriteFormBuilder;
//import com.ll.utils.builder.JoinFormBuilder;
//import jakarta.servlet.http.Cookie;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.ResultActions;
//
//import java.time.Duration;
//import java.time.LocalDate;
//
//import static com.ll.goohaeyou.global.config.AppConfig.objectMapper;
//import static org.hamcrest.Matchers.hasSize;
//import static org.hamcrest.Matchers.is;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//@SpringBootTest
//@AutoConfigureMockMvc
//@ActiveProfiles("test")
//public class MypageControllerIntegrationTest {
//    @Autowired
//    private MockMvc mockMvc;
//    @Autowired
//    private JwtTokenProvider jwtTokenProvider;
//    @Autowired
//    private MemberService memberService;
//    @Autowired
//    private JobPostService jobPostService;
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//    private static final Duration ACCESS_TOKEN_DURATION = Duration.ofMinutes(30);
//    private static final String ACCESS_TOKEN_COOKIE_NAME = "access_token";
//    @DisplayName("내 정보 조회 - 성공")
//    @Test
//    void getMemberDetailsSuccess() throws Exception {
//        // Given
//        JoinForm joinForm = JoinForm.builder()
//                .username("user1")
//                .name("지원자")
//                .password(passwordEncoder.encode("password123"))
//                .phoneNumber("01012345678")
//                .gender(Gender.MALE)
//                .location("경기 성남시 분당구 판교역로 4")
//                .birth(LocalDate.of(2000, 1, 1))
//                .build();
//        memberService.join(joinForm);
//        memberService.login(new LoginForm(joinForm.getUsername(), joinForm.getPassword()));
//        Member member = memberService.getMember(joinForm.getUsername());
//        String accessToken = jwtTokenProvider.generateToken(member, ACCESS_TOKEN_DURATION);
//
//        // When
//        ResultActions resultActions = mockMvc.perform(get("/api/member")
//                        .cookie(new Cookie(ACCESS_TOKEN_COOKIE_NAME, accessToken))
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andDo(print());
//
//        // Then
//        resultActions.andExpect(status().isOk())
//                .andExpect(jsonPath("$.statusCode").value(200))
//                .andExpect(jsonPath("$.message").value("OK"))
//                .andExpect(jsonPath("$.data.id").value(member.getId()))
//                .andExpect(jsonPath("$.data.username").value("user1"))
//                .andExpect(jsonPath("$.data.name").value("지원자"))
//                .andExpect(jsonPath("$.data.phoneNumber").value("01012345678"))
//                .andExpect(jsonPath("$.data.gender").value("MALE"))
//                .andExpect(jsonPath("$.data.location").value("경기 성남시 분당구 판교역로 4"))
//                .andExpect(jsonPath("$.data.birth").value("2000-01-01"));
//    }
//
//    @DisplayName("내 공고 조회 - 성공")
//    @Test
//    void detailMyPostsSuccess() throws Exception {
//        // Given
//        memberService.join(new JoinFormBuilder().setUsername("recruiter").build());
//        jobPostService.writePost(
//                "recruiter", new JobPostWriteFormBuilder().setTitle("공고 제목1").build());
//        jobPostService.writePost(
//                "recruiter", new JobPostWriteFormBuilder().setTitle("공고 제목2").build());
//        Member member = memberService.getMember("recruiter");
//        String accessToken = jwtTokenProvider.generateToken(member, ACCESS_TOKEN_DURATION);
//        // When
//        ResultActions resultActions = mockMvc.perform(get("/api/member/myposts")
//                        .cookie(new Cookie(ACCESS_TOKEN_COOKIE_NAME, accessToken))
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andDo(print());
//        // Then
//        resultActions.andExpect(status().isOk())
//                .andExpect(jsonPath("$.statusCode").value(200))
//                .andExpect(jsonPath("$.message").value("OK"))
//                .andExpect(jsonPath("$.data", hasSize(2)))
//                .andExpect(jsonPath("$.data[0].author", is("recruiter")))
//                .andExpect(jsonPath("$.data[0].title", is("공고 제목1")))
//                .andExpect(jsonPath("$.data[1].author", is("recruiter")))
//                .andExpect(jsonPath("$.data[1].title", is("공고 제목2")));
//    }
//
//    @DisplayName("내 정보 수정 - 성공")
//    @Test
//    void putMemberDetailsSuccess() throws Exception {
//        // Given
//        memberService.join(new JoinFormBuilder().setUsername("user12").build());
//        Member member = memberService.getMember("user12");
//        String accessToken = jwtTokenProvider.generateToken(member, ACCESS_TOKEN_DURATION);
//
//        // 수정할 정보를 담은 폼 생성
//        MemberForm.Modify updateForm = new MemberForm.Modify();
//        updateForm.setPassword("newpassword123");
//        updateForm.setGender(Gender.MALE);
//        updateForm.setLocation("서울특별시");
//        updateForm.setBirth(LocalDate.of(1991, 1, 10));
//
//        // When
//        ResultActions resultActions = mockMvc.perform(put("/api/member")
//                        .cookie(new Cookie(ACCESS_TOKEN_COOKIE_NAME, accessToken))
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(updateForm))) // 수정할 정보를 JSON 형식으로 전송
//                .andDo(print());
//
//        // Then
//        resultActions.andExpect(status().isOk())
//                .andExpect(jsonPath("$.statusCode").value(204))
//                .andExpect(jsonPath("$.message").value("No Content"))
//                .andExpect(jsonPath("$.resultType").value("SUCCESS"));
//    }
//}