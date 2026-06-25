package com.harsh.ai_code_review_system;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@EnableRabbit
@ConfigurationPropertiesScan
public class AiCodeReviewSystemApplication {

	public static void main(String[] args) {

		SpringApplication.run(AiCodeReviewSystemApplication.class, args);
	}

}
