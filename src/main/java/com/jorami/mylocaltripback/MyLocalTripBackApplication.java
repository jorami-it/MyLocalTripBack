package com.jorami.mylocaltripback;

import com.jorami.mylocaltripback.model.Role;
import com.jorami.mylocaltripback.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MyLocalTripBackApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyLocalTripBackApplication.class, args);
	}

	@Bean
	public CommandLineRunner runner(RoleRepository roleRepository) {
		return args -> {
			if (roleRepository.findRoleByName("USER").isEmpty()) {
				roleRepository.save(Role.builder().name("USER").build());
			}
		};
	}
}
