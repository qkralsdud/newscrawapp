package com.cos.newscrawapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class NewscrawappApplication {

	public static void main(String[] args) {
		SpringApplication.run(NewscrawappApplication.class, args);
	}

}
