package com.nicholaswallace.lessons_plan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController

public class LessonsPlanApplication {

	public static void main(String[] args) {
		SpringApplication.run(LessonsPlanApplication.class, args);
	}

	@GetMapping
	public String helloWorld() {
		return "Hello World Spring";
	}

}
