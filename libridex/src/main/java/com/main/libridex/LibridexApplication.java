package com.main.libridex;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import com.main.libridex.utils.CloudinaryUtils;

@SpringBootApplication
public class LibridexApplication {

	public static void main(String[] args) {
		SpringApplication.run(LibridexApplication.class, args);

	}

	@Bean
	CommandLineRunner init(CloudinaryUtils cloudinaryUtils) {
		return (_) -> {

		};
	}
}
