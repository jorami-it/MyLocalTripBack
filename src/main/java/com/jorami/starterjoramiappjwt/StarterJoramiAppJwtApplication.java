package com.jorami.starterjoramiappjwt;

import com.jorami.starterjoramiappjwt.model.Role;
import com.jorami.starterjoramiappjwt.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class StarterJoramiAppJwtApplication {

	public static void main(String[] args) {
		SpringApplication.run(StarterJoramiAppJwtApplication.class, args);
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
