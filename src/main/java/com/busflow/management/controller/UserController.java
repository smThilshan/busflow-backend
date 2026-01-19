package com.busflow.management.controller;

import com.busflow.management.config.CustomUserDetails;
import com.busflow.management.dto.CreateConductorRequestDTO;
import com.busflow.management.dto.UserResponseDTO;
import com.busflow.management.entity.User;
import com.busflow.management.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

//    create new conductor (OWNER only)

    @PostMapping("/conductors")
    public ResponseEntity<UserResponseDTO> createConductor(@Valid @RequestBody CreateConductorRequestDTO request, @AuthenticationPrincipal CustomUserDetails userDetails) {
        UserResponseDTO response = userService.createConductor(request,userDetails.getUser());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


//   Get all conductors

    @GetMapping("/conductors")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<List<UserResponseDTO>> getAllConductors(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        List<UserResponseDTO> conductors = userService.getAllConductors(userDetails.getUser());
        return ResponseEntity.ok(conductors);
    }

//   Get conductor by ID

    @GetMapping("/conductors/{conductorId}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<UserResponseDTO> getConductorById(
            @PathVariable Long conductorId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        UserResponseDTO conductor = userService.getConductorById(conductorId, userDetails.getUser());
        return ResponseEntity.ok(conductor);
    }



}
