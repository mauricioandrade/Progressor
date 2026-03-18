package com.mauricioandrade.progressor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ProgressorApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProgressorApplication.class, args);
	}

}
