package com.busflow.management.service;

import com.busflow.management.exception.InvalidCredentialsException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PasswordAuthService {
    private final PasswordEncoder passwordEncoder;

    public String hash(String rawPassword) {

        return passwordEncoder.encode(rawPassword);
    }

    public void validate(String rawPassword, String hashedPassword) {
        if (!passwordEncoder.matches(rawPassword, hashedPassword)) {
            throw new InvalidCredentialsException("Invalid username or password");
        }
    }
}
