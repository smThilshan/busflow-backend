package com.busflow.management.service;

import com.busflow.management.exception.InvalidCredentialsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PasswordAuthService {
    public void validate(String rawPassword, String storedPassword) {
        if (!storedPassword.equals(rawPassword)) {
            throw new InvalidCredentialsException("Invalid password");
        }
    }
}
