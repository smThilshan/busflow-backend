package com.busflow.management.service;

import com.busflow.management.dto.LoginRequestDTO;
import com.busflow.management.dto.LoginResponseDTO;
import com.busflow.management.entity.User;
import com.busflow.management.exception.InvalidCredentialsException;
import com.busflow.management.mapper.AuthMapper;
import com.busflow.management.repository.UserRepository;
import com.busflow.management.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

//    private final UserAuthService userAuthService;
    private final PasswordAuthService passwordAuthService;
    private final JwtService jwtService;
    private final UserRepository userRepository;
//    private final AuthMapper authMapper;


    public LoginResponseDTO login(LoginRequestDTO request) {
//        User user = userAuthService.getUserbyUsername(request.getUsername());
//
//        passwordAuthService.validate(request.getPassword(), user.getPassword());
//        String token = jwtService.generateToken(user);
//
//        return  new LoginResponseDTO(user.getId(), user.getRole().name(), token);

        // Try to fetch user
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid username or password"));

        // Validate password
        passwordAuthService.validate(request.getPassword(), user.getPassword());

        // Generate JWT
        String token = jwtService.generateToken(user);

        return new LoginResponseDTO(user.getId(), user.getRole().toString(), token);
    }



}
