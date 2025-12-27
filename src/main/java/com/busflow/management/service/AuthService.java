package com.busflow.management.service;

import com.busflow.management.dto.LoginRequestDTO;
import com.busflow.management.dto.LoginResponseDTO;
import com.busflow.management.entity.User;
import com.busflow.management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
//    private final UserRepository userRepository;
//
//    public AuthService(UserRepository userRepository) {
//        this.userRepository = userRepository;
//    }
//
//    public LoginResponseDTO login(LoginRequestDTO request) {
//
//        // 1. Find user
//        User user = userRepository.findByUsername(request.getUsername())
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        // 2. Match password (PLAIN TEXT for MVP)
//        if (!user.getPassword().equals(request.getPassword())) {
//            throw new RuntimeException("Invalid password");
//        }
//
//        // 3. Return response
//        return new LoginResponseDTO(
//                user.getId(),
//                user.getRole().name()
//        );
//    }

    private final UserAuthService userAuthService;
    private final PasswordAuthService passwordAuthService;

    public LoginResponseDTO login(LoginRequestDTO request) {
        User user = userAuthService.getUserbyUsername(request.getUsername());

        passwordAuthService.validate(request.getPassword(), user.getPassword());

        return  new LoginResponseDTO(user.getId(), user.getRole().name());
    }



}
