package com.arun.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@ComponentScan(basePackages = "com.arun")
@EnableAutoConfiguration
public class ReactiveConfig {
	
	public static void main(String[] args) {
		SpringApplication.run(ReactiveConfig.class, args);
	}
}
