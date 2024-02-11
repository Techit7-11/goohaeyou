package com.ll.gooHaeYu.global.config;

import com.ll.gooHaeYu.domain.member.member.repository.RefreshTokenRepository;
import com.ll.gooHaeYu.domain.member.member.service.MemberService;
import com.ll.gooHaeYu.global.security.CustomUserDetailsService;
import com.ll.gooHaeYu.global.security.JwtFilter;
import com.ll.gooHaeYu.global.security.JwtTokenProvider;
import com.ll.gooHaeYu.global.security.OAuth.OAuth2AuthorizationRequestBasedOnCookieRepository;
import com.ll.gooHaeYu.global.security.OAuth.OAuth2SuccessHandler;
import com.ll.gooHaeYu.global.security.OAuth.OAuth2UserCustomService;
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
                .cors()
                .and()
                .authorizeHttpRequests(requests -> {
                    requests
                            .requestMatchers("/h2-console/**").permitAll()
                            .requestMatchers("/api/member/socialLogin/**").permitAll()
                            .requestMatchers("/oauth2/authorization/**").permitAll() // OAuth 2.0 인증 엔드포인트에 대한 접근 허용
                            .requestMatchers("/login", "/api/member/join", "api/member/login", "api/member/logout").permitAll()
                            .requestMatchers(HttpMethod.GET, "/api/job-posts/**", "/api/job-posts",
                                    "/api/job-posts/search",
                                    "/api/post-comment/{postId}").permitAll()
                            .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                            .requestMatchers("/**").permitAll()
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
