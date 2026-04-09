package com.nicholaswallace.lessons_plan;

import com.nicholaswallace.lessons_plan.config.AuthProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
@EnableConfigurationProperties(AuthProperties.class)
public class LessonsPlanApplication {

	public static void main(String[] args) {
		SpringApplication.run(LessonsPlanApplication.class, args);
	}
}
