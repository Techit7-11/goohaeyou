package com.ll.utils;

import com.ll.gooHaeYu.domain.member.member.dto.JoinForm;
import com.ll.gooHaeYu.domain.member.member.dto.LoginForm;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

@Component
public class TestAuthenticationUtil {

    private final TestRestTemplate restTemplate;
    private final String baseUrl;

    public TestAuthenticationUtil(TestRestTemplate restTemplate) {
        this.baseUrl = "http://localhost:8083/api/member";
        this.restTemplate = restTemplate;
    }

    public void registerMember(JoinForm joinForm) {
        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl + "/join", joinForm, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    public HttpHeaders loginAndGetCookies(LoginForm loginForm) {
        ResponseEntity<String> loginResponse = restTemplate.postForEntity(baseUrl + "/login", loginForm, String.class);
        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<String> setCookies = loginResponse.getHeaders().get(HttpHeaders.SET_COOKIE);

        HttpHeaders headers = new HttpHeaders();
        if (setCookies != null) {
            for (String cookie : setCookies) {
                if (cookie.startsWith("access_token=")) {
                    String accessToken = extractToken(cookie, "access_token");
                    headers.add(HttpHeaders.COOKIE, "access_token=" + accessToken);
                } else if (cookie.startsWith("refresh_token=")) {
                    String refreshToken = extractToken(cookie, "refresh_token");
                    headers.add(HttpHeaders.COOKIE, "refresh_token=" + refreshToken);
                }
            }
        }
        return headers;
    }

    public String extractToken(String cookie, String tokenName) {
        Pattern pattern = Pattern.compile(tokenName + "=([^;]*)");
        Matcher matcher = pattern.matcher(cookie);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}
