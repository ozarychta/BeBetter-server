package com.ozarychta.bebetter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.TimeZone;

@Controller
@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
@EnableTransactionManagement
public class BeBetterApplication {


	@RequestMapping("/")
	@ResponseBody
	String home() {
		return "BeBetter!";
	}

	public static void main(String[] args) {
		SpringApplication.run(BeBetterApplication.class, args);
		TimeZone.setDefault(TimeZone.getTimeZone("Etc/UTC"));
	}

}
