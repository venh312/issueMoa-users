package com.issuemoa.users;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class IssueMoaUsersApplication {

	public static void main(String[] args) {
		SpringApplication.run(IssueMoaUsersApplication.class, args);
	}

}
