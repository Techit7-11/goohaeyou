package com.ll.gooHaeYu.global.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CustomWebMvcConfig implements WebMvcConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(CustomWebMvcConfig.class);

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(
                        AppConfig.getSiteFrontUrl()
                )
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/members/images/**")
                .addResourceLocations("file:/Users/mingulee/Desktop/uploads/");
        logger.debug("정적 리소스 경로 설정: /members/images/** -> file:/Users/mingulee/Desktop/uploads/");
    }
}
