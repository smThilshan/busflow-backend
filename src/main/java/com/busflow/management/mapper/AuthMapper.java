package com.busflow.management.mapper;

import com.busflow.management.dto.LoginResponseDTO;
import com.busflow.management.entity.User;
import org.springframework.stereotype.Component;

@Component
public class AuthMapper {
    public LoginResponseDTO toLoginResponse(User user, String token) {
        return new LoginResponseDTO(
                user.getId(),
                user.getRole().name(),
                token
        );
    }
}
