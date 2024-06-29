package com.ll.config;

import com.ll.utils.TestAuthenticationUtil;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestAuthConfig {

    @Bean
    public TestRestTemplate testRestTemplate() {
        return new TestRestTemplate();
    }

    @Bean
    public TestAuthenticationUtil testAuthenticationUtil(TestRestTemplate testRestTemplate) {
        return new TestAuthenticationUtil(testRestTemplate);
    }
}
