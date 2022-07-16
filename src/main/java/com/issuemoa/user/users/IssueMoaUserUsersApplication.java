package com.issuemoa.user.users;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


@EnableJpaAuditing
@SpringBootApplication
public class IssueMoaUserUsersApplication {

	public static void main(String[] args) {
		SpringApplication.run(IssueMoaUserUsersApplication.class, args);
	}

}
