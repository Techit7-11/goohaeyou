package com.ll.gooHaeYu.global.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@Configuration("jwt")
public class JwtProperties {
    private String secretKey;
}
