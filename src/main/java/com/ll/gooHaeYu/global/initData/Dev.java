package com.ll.gooHaeYu.global.initData;

import com.ll.gooHaeYu.global.config.AppConfig;
import com.ll.gooHaeYu.standard.base.util.Ut;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;

@Profile("dev")
@Configuration
@RequiredArgsConstructor
public class Dev {
    @Bean
    @Order(4)
    ApplicationRunner initDev() {
        return args -> {
            String backUrl = AppConfig.getSiteBackUrl();
            String cmd = "npx openapi-typescript " + backUrl + "/v3/api-docs -o ./front/src/lib/types/api/v1/schema.d.ts";
            Ut.Cmd.runAsync(cmd);
        };
    }
}
