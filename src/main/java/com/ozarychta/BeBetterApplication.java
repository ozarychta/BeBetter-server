package com.ozarychta;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
public class BeBetterApplication {


	@RequestMapping("/")
	@ResponseBody
	String home() {
		return "BeBetter!";
	}

	public static void main(String[] args) {
		SpringApplication.run(BeBetterApplication.class, args);
	}

}
