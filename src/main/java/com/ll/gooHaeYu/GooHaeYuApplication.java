package com.ll.gooHaeYu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class GooHaeYuApplication {

	public static void main(String[] args) {
		SpringApplication.run(GooHaeYuApplication.class, args);
	}

}
