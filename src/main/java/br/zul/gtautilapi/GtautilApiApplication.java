package br.zul.gtautilapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GtautilApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(GtautilApiApplication.class, args);
	}

}
