package com.ll.goohaeyou.global.config;

import com.ll.goohaeyou.auth.domain.RefreshTokenRepository;
import com.ll.goohaeyou.member.member.application.MemberService;
import com.ll.goohaeyou.auth.application.CustomUserDetailsService;
import com.ll.goohaeyou.auth.infrastructure.JwtFilter;
import com.ll.goohaeyou.auth.application.JwtTokenProvider;
import com.ll.goohaeyou.auth.infrastructure.OAuth2AuthorizationRequestBasedOnCookieRepository;
import com.ll.goohaeyou.auth.application.OAuth2SuccessHandler;
import com.ll.goohaeyou.auth.application.OAuth2UserCustomService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity // 기본 웹 보안 활성화
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;
    private final MemberService memberService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final OAuth2UserCustomService oAuth2UserCustomService;

    @Bean
    public WebSecurityCustomizer configure() {
        return (web) -> web.ignoring()
                .requestMatchers("/h2-console/**")
                .requestMatchers("/static/**");
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        return httpSecurity
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(requests -> {
                    requests
                            .requestMatchers("/h2-console/**").permitAll()
                            .requestMatchers("/api/member/socialLogin/**").permitAll()
                            .requestMatchers("/oauth2/authorization/**").permitAll() // OAuth 2.0 인증 엔드포인트에 대한 접근 허용
                            .requestMatchers("/login", "/api/member/join", "/api/member/login").permitAll()
                            .requestMatchers(HttpMethod.GET, "/api/job-posts/**",
                                    "/api/post-comment/{postId}", "/api/members/image/**",
                                    "/api/job-post/images/**", "/api/categories/**").permitAll()
                            .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                            .requestMatchers("/ready").permitAll()
                            .anyRequest()
                            .authenticated();

                })
                .sessionManagement(
                        sessionManagement ->
                                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(new JwtFilter(jwtTokenProvider, customUserDetailsService), UsernamePasswordAuthenticationFilter.class)
                .oauth2Login(oauth2Login -> {
                    oauth2Login
                            .authorizationEndpoint()
                            .authorizationRequestRepository(oAuth2AuthorizationRequestBasedOnCookieRepository())
                            .and()
                            .successHandler(oAuth2SuccessHandler())
                            .userInfoEndpoint()
                            .userService(oAuth2UserCustomService);
                })

                .exceptionHandling(exceptionHandling -> {
                    exceptionHandling
                            .defaultAuthenticationEntryPointFor(
                                    new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                                    new AntPathRequestMatcher("/api/**")
                            );
                })
                .build();
    }

    @Bean
    public OAuth2SuccessHandler oAuth2SuccessHandler() {
        return new OAuth2SuccessHandler(jwtTokenProvider,
                refreshTokenRepository,
                oAuth2AuthorizationRequestBasedOnCookieRepository(),
                memberService
        );
    }

    @Bean
    public JwtFilter jwtFilter() {
        return new JwtFilter(jwtTokenProvider, customUserDetailsService);
    }

    @Bean
    public OAuth2AuthorizationRequestBasedOnCookieRepository oAuth2AuthorizationRequestBasedOnCookieRepository() {
        return new OAuth2AuthorizationRequestBasedOnCookieRepository();
    }
}
