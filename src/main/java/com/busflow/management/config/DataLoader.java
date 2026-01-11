package com.busflow.management.config;

import com.busflow.management.entity.User;
import com.busflow.management.enums.Role;
import com.busflow.management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataLoader {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner loadUsers() {
        return args -> {

            if (userRepository.count() == 0) {

                User owner = User.builder()
                        .username("owner1")
                        .password(passwordEncoder.encode("password123"))
                        .role(Role.OWNER)
                        .build();

                User conductor = User.builder()
                        .username("conductor1")
                        .password(passwordEncoder.encode("password123"))
                        .role(Role.CONDUCTOR)
                        .build();

                userRepository.save(owner);
                userRepository.save(conductor);
            }
        };
    }
}
