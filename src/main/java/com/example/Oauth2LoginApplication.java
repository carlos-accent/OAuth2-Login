package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@Configuration
@ComponentScan
@EnableAutoConfiguration
@EnableResourceServer
@RestController
public class Oauth2LoginApplication {

	public static void main(String[] args) {
		SpringApplication.run(Oauth2LoginApplication.class, args);
	}
	@RequestMapping("/")
	public String home() {
		return "Hello World";
	}

}
