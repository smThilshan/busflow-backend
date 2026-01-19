package com.busflow.management.service;
import com.busflow.management.dto.CreateConductorRequestDTO;
import com.busflow.management.dto.UserResponseDTO;
import com.busflow.management.entity.User;
import com.busflow.management.enums.Role;
import com.busflow.management.exception.ResourceNotFoundException;
import com.busflow.management.exception.UnauthorizedException;
import com.busflow.management.repository.BusAssignmentRepository;
import com.busflow.management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final BusAssignmentRepository busAssignmentRepository;

//    Create new conductor
    @Transactional
    public UserResponseDTO createConductor(CreateConductorRequestDTO request, User owner) {

//     Validate owner role
        if (owner.getRole() != Role.OWNER) {
            throw new UnauthorizedException("Only owners can create conductors");
        }

//     Check the user is existing
        if (userRepository.existsByUsername(request.getUsername())){
            throw new IllegalArgumentException("Username is already exist:" + request.getPassword() );
        }

//     Validate the password is strong
        validatePassword(request.getPassword());

//     Create conductor
        User conductor = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.CONDUCTOR)
                .build();

        User savedConductor = userRepository.save(conductor);
        log.info("Owner {} Created conductor: {}",owner.getUsername() , savedConductor.getId());

        return mapToResponseDTO(savedConductor);

    }

//    Get all conductors created by this owner
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllConductors(User owner) {

//     Validate owner role
        if (owner.getRole() != Role.OWNER) {
            throw new UnauthorizedException("Only owners can view conductors");
        }

        List<User> conductors = userRepository.findByRole(Role.CONDUCTOR);

        return conductors.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());

    }

//   Get conductor by ID with validation
@Transactional (readOnly = true)
public UserResponseDTO getConductorById(Long conductorId, User owner) {
    // Validate owner role
    if (owner.getRole() != Role.OWNER) {
        throw new UnauthorizedException("Only owners can view conductor details");
    }

    User conductor = userRepository.findById(conductorId).orElseThrow(() -> new ResourceNotFoundException("Conductor not found with id: " + conductorId));

    if (conductor.getRole() != Role.CONDUCTOR) {
        throw new UnauthorizedException("User is not a conductor");
    }
    return mapToResponseDTO(conductor);
}


    /**
     * Map User entity to UserResponseDTO
     */
    private UserResponseDTO mapToResponseDTO(User user) {

        // Get assigned buses count if conductor
        int assignedBusesCount = 0;
        if (user.getRole() == Role.CONDUCTOR) {
            assignedBusesCount = busAssignmentRepository
                    .findByUserIdAndIsActiveTrue(user.getId())
                    .size();
        }

        return UserResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(String.valueOf(user.getRole()))
                .assignedBusesCount(assignedBusesCount)
                .createdAt(user.getCreatedAt())
                .build();
    }

    /**
     * Validate password strength
     */
    private void validatePassword(String password) {
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long");
        }
    }
//    Late make this validation difficultly
}


